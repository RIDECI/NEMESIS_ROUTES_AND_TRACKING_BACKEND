package edu.dosw.rideci.infrastructure.persistance.repository;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import edu.dosw.rideci.application.port.out.GoogleMapsRepositoryPort;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.exceptions.ExternalServiceException;
import edu.dosw.rideci.exceptions.RouteNotFoundException;
import edu.dosw.rideci.infrastructure.persistance.Entity.RouteDocument;
import edu.dosw.rideci.infrastructure.persistance.mapper.RouteMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
@Slf4j
public class GoogleMapsAdapter implements GoogleMapsRepositoryPort {

    private final GeoApiContext geoApiContext;
    private final GeolocationUtils geolocationUtils;
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;

    @Override
    public Route calculateRoute(Location origin, Location destiny) {

        if (destiny.getDirection() == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }

        try {

            DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                    .origin(origin.getDirection())
                    .destination(destiny.getDirection())
                    .mode(TravelMode.DRIVING)
                    .await();

            if (result.routes.length > 0) {
                DirectionsRoute route = result.routes[0];
                DirectionsLeg leg = route.legs[0];

                return Route.builder()
                        .totalDistance(leg.distance.inMeters)
                        .estimatedTime(leg.duration.inSeconds)
                        .polyline(route.overviewPolyline.getEncodedPath())
                        .build();
            }

        } catch (Exception e) {
            log.error("Google Maps calculateRoute failed for origin={} destiny={}", origin, destiny, e);
            throw new ExternalServiceException("Error connecting with Google Maps", e);
        }

        return null;
    }

    @Override
    public Route calculateRouteWithWayPoints(Location origin, Location destiny, List<PickUpPoint> pickUpPoints) {
        try {

            String originStr = origin.getLatitude() + "," + origin.getLongitude();
            String destStr = destiny.getLatitude() + "," + destiny.getLongitude();

            String[] waypointArray = pickUpPoints.stream()
                    .map(p -> p.getPassengerLocation().getLatitude() + "," + p.getPassengerLocation().getLongitude())
                    .toArray(String[]::new);

            DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                    .origin(originStr)
                    .destination(destStr)
                    .mode(TravelMode.DRIVING)
                    .waypoints(waypointArray)
                    .optimizeWaypoints(true)
                    .await();

            if (result.routes.length > 0) {
                DirectionsRoute route = result.routes[0];

                int[] orderIndex = route.waypointOrder;

                Long totalDistance = 0L;
                Long totalDuration = 0L;

                for (int i = 0; i < route.legs.length; i++) {
                    DirectionsLeg leg = route.legs[i];

                    totalDistance += leg.distance.inMeters;
                    totalDuration += leg.duration.inSeconds;

                    if (i < orderIndex.length) {
                        
                        int originalIndex = orderIndex[i];

                        PickUpPoint point = pickUpPoints.get(originalIndex);

                        point.setDistanceFromPreviousStop(leg.distance.inMeters);
                        point.setEstimatedTimeToPick(leg.duration.inSeconds);

                        point.setOrder(i + 1);
                    }
                }

                return Route.builder()
                        .totalDistance(totalDistance)
                        .estimatedTime(totalDuration)
                        .polyline(route.overviewPolyline.getEncodedPath())
                        .build();
            }
        } catch (Exception e) {
            log.error("Google Maps calculateRouteWithWayPoints failed origin={} destiny={} waypoints={} ", origin,
                    destiny, pickUpPoints, e);
            throw new ExternalServiceException("Error connecting with Google Maps", e);
        }
        return null;
    }

    public boolean isPickUpLocationOnPath(double pickUpPointLat, double pickUpPointLon, String encodedPolyline,
            double toleranceMeters) {

        List<LatLng> polylinePoints = PolylineEncoding.decode(encodedPolyline);

        for (int i = 0; i < polylinePoints.size() - 1; i++) {
            LatLng start = polylinePoints.get(i);
            LatLng end = polylinePoints.get(i + 1);

            double distanceToSegment = geolocationUtils.distanceToSegment(pickUpPointLat, pickUpPointLon, start.lat,
                    start.lng, end.lat, end.lng);

            if (distanceToSegment < toleranceMeters) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Scheduled(fixedRate = 120000)
    public void scheduledEtaRecalculation(){

        List<RouteDocument> activeRoutes = routeRepository.findAll();

        for(RouteDocument routeDoc : activeRoutes){
            Route route = routeMapper.toDomain(routeDoc);

            if(route.getTravelTracking() == null){
                log.info("Skipping ETA calculation for route:" + route.getId() + "because TravelTracking is null");
                continue;
            }

            if(route.getTravelTracking().getLastLocation() != null){
                recalculateETA(route.getId(), route.getTravelTracking().getLastLocation(), route.getDestiny());
            } else {
                throw new NullPointerException("Cannot recalculate because location is null");
            }
            
        }
    }

    @Override
    @Async
    public void recalculateETA(String routeId, Location lastLocation, Location destiny){
        try{

            Route googleData = calculateRoute(lastLocation, destiny);

            RouteDocument routeDoc = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route with id: {id} was not found"));

            Route route = routeMapper.toDomain(routeDoc);

            route.setEstimatedTime(googleData.getEstimatedTime());
            route.setPolyline(googleData.getPolyline());

            RouteDocument updateRouteDoc = routeMapper.toDocument(route);
            routeRepository.save(updateRouteDoc);
        } catch (Exception e){
            throw new ExternalServiceException("Error during async ETA calculation for route: " + routeId);
        }
    }
}
