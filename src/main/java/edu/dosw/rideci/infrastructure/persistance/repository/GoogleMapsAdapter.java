package edu.dosw.rideci.infrastructure.persistance.repository;

import java.util.List;
import org.springframework.stereotype.Repository;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;

import edu.dosw.rideci.application.port.in.MapsServicePort;
import edu.dosw.rideci.application.port.out.GoogleMapsRepositoryPort;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.exceptions.ExternalServiceException;
import edu.dosw.rideci.infrastructure.config.GoogleMapsConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Repository
public class GoogleMapsAdapter implements GoogleMapsRepositoryPort {

    private final GoogleMapsConfig googleMapsConfig;

    @Override
    public Route calculateRoute(Location origin, Location destination){
        try {

            DirectionsResult result = DirectionsApi.newRequest(googleMapsConfig.geoApiContext())
                .origin(origin.getAddress())
                .destination(destination.getAddress())
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
            throw new ExternalServiceException("Error connecting with Google Maps");
        }

        return null;
    }

    @Override
    public Long recalculateETA(Long newETA){
        return null;
    }

    @Override
    public Route calculateRouteWithWayPoints(Location origin, Location destination, List<PickUpPoint> pickUpPoints){
        try{

            String[] waypointArray = pickUpPoints.stream()
                .map(p -> p.getPassengerLocation().getAddress())
                .toArray(String [] :: new);

            DirectionsResult result = DirectionsApi.newRequest(googleMapsConfig.geoApiContext())
                .origin(origin.getAddress())
                .destination(destination.getAddress())
                .mode(TravelMode.DRIVING)
                .waypoints(waypointArray)
                .optimizeWaypoints(true)
                .trafficModel(TrafficModel.BEST_GUESS)
                .await();

            if(result.routes.length > 0) {
                DirectionsRoute route = result.routes[0];

                int[] orderIndex = route.waypointOrder;

                Long totalDistance = 0L;
                Long totalDuration = 0L;

                for(int i=0; i < route.legs.length; i++){ 
                    DirectionsLeg leg = route.legs[i];

                    totalDistance += leg.distance.inMeters;
                    totalDuration += leg.duration.inSeconds;

                    if(i < pickUpPoints.size()){
                        PickUpPoint point = pickUpPoints.get(i);

                        point.setDistanceFromPreviousStop(leg.distance.inMeters);
                        point.setEstimatedTimeToPick(leg.duration.inSeconds);
                    }
                }

                return Route.builder()
                    .totalDistance(totalDistance)
                    .estimatedTime(totalDuration)
                    .polyline(route.overviewPolyline.getEncodedPath())
                    .build();
            }
        } catch (Exception e){
            throw new ExternalServiceException("Error connecting with Google Maps");
        }
        return null;
    }
    
}
