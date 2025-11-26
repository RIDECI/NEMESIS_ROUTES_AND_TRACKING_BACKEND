package edu.dosw.rideci.infrastructure.persistance.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import edu.dosw.rideci.application.events.command.CreateRouteCommand;
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

@RequiredArgsConstructor
@Repository
public class GeolocalizationAdapter implements GeolocalizationRepositoryPort {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;

    @Override
    public Route createRoute(CreateRouteCommand event) {

        Route route = Route.builder()
                .travelId(event.getTravelId())
                .origin(event.getOrigin())
                .destination(event.getDestiny())
                .departureDateAndTime(event.getDepartureDateAndTime())
                .totalDistance(0.0) // toca calcularla, dos formas de hacerlo
                .build();

        RouteDocument createdRoute = routeMapper.toDocument(route);

        RouteDocument savedRoute = routeRepository.save(createdRoute);

        return routeMapper.toDomain(savedRoute);

    }

    @Override
    public Route getRouteInformation(Long routeId){

        RouteDocument route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException("Route not found with id: {id}"));

        return routeMapper.toDomain(route);

    }

    @Override
    public List<PickUpPoint> getPickUpPoints(Long routeId){

        RouteDocument route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException("Route Not Found with id: {id}"));

        route.getPickupPoints();

        return routeMapper.toPickUpPointDomainList(route.getPickupPoints());
    }

    @Override
    public Location getRealTimePosition(Long routeId){

        RouteDocument route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException("Route not found with id: {id}"));
        
        LocationDocument actualLocation = route.getTravelTracking().getLastLocation();

        return routeMapper.toLocationDomain(actualLocation);
        
    }

    @Override
    public void updateIntervalSeconds(Long routeId, int newInterval){
        
        RouteDocument route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException("Route not found with id: {id}"));
        
        route.getTravelTracking().getTrackingConfiguration().setUpdateIntervalSeconds(newInterval);
    }

}
