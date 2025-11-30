package edu.dosw.rideci.infrastructure.persistance.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.application.port.in.MapsServicePort;
import edu.dosw.rideci.application.port.out.GeolocalizationRepositoryPort;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.domain.model.TrackingConfiguration;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.infrastructure.persistance.Entity.LocationDocument;
import edu.dosw.rideci.infrastructure.persistance.Entity.RouteDocument;
import edu.dosw.rideci.infrastructure.persistance.Entity.TravelTrackingDocument;
import edu.dosw.rideci.infrastructure.persistance.mapper.RouteMapper;
import lombok.RequiredArgsConstructor;
import edu.dosw.rideci.exceptions.RouteNotFoundException;
import edu.dosw.rideci.exceptions.ExternalServiceException;

@RequiredArgsConstructor
@Repository
public class GeolocalizationAdapter implements GeolocalizationRepositoryPort {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final MapsServicePort mapsServicePort;

    @Override
    public Route createRoute(CreateRouteCommand event) {
        Route googleData = mapsServicePort.calculateRoute(event.getOrigin(), event.getDestiny());

        Route route = Route.builder()
                .travelId(event.getTravelId())
                .origin(event.getOrigin())
                .destination(event.getDestiny())
                .departureDateAndTime(event.getDepartureDateAndTime())
                .totalDistance(googleData.getTotalDistance())
                .estimatedTime(googleData.getEstimatedTime())
                .polyline(googleData.getPolyline())
                .build();

        RouteDocument createdRoute = routeMapper.toDocument(route);

        RouteDocument savedRoute = routeRepository.save(createdRoute);

        return routeMapper.toDomain(savedRoute);

    }

    @Override
    public Route getRouteInformation(String routeId) {

        RouteDocument route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: {id}"));

        return routeMapper.toDomain(route);

    }

    @Override
    public List<PickUpPoint> getPickUpPoints(String routeId) {

        RouteDocument route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route Not Found with id: {id}"));

        return routeMapper.toPickUpPointDomainList(route.getPickUpPoints());
    }

    @Override
    public Location getRealTimePosition(String routeId) {

        RouteDocument actualRoute = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route with id: {id} was not found"));

        LocationDocument actualLocation = actualRoute.getTravelTracking().getLastLocation();

        return routeMapper.toLocationDomain(actualLocation);

    }

    @Override
    public void updateIntervalSeconds(String routeId, int newInterval) {

        RouteDocument route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route with id: {id} was not found"));

        route.getTravelTracking().getTrackingConfiguration().setUpdateIntervalSeconds(newInterval);
    }

    @Override
    public Location updateLocation(String routeId, Location newLocation) {

        RouteDocument route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route with id: {id} was not found"));

        LocationDocument actualLocation = route.getTravelTracking().getLastLocation();

        if (actualLocation.getAccuracy() > 50.0) {
            return routeMapper.toLocationDomain(actualLocation);
        }

        Location updatedLocation = Location.builder()
                .latitude(newLocation.getLatitude())
                .longitude(newLocation.getLongitude())
                .timeStamp(LocalDateTime.now())
                .speed(newLocation.getSpeed())
                .placeId(newLocation.getPlaceId())
                .address(newLocation.getAddress())
                .accuracy(newLocation.getAccuracy())
                .build();

        route.getTravelTracking().getLocationHistory().add(actualLocation);
        route.getTravelTracking().setLastLocation(actualLocation);
        route.getTravelTracking().setLastUpdate(LocalDateTime.now());

        LocationDocument updatedLocationDocument = routeMapper.toLocationDocumentEmbeddable(updatedLocation);

        routeRepository.save(route);

        return updatedLocation;

    }

}
