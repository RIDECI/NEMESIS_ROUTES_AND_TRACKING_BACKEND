package edu.dosw.rideci.infrastructure.persistance.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.rabbitmq.client.Return;

import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.application.port.in.CalculateRouteWithWayPointsUseCase;
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
import lombok.extern.slf4j.Slf4j;
import edu.dosw.rideci.exceptions.RouteNotFoundException;
import edu.dosw.rideci.exceptions.TimeOutException;
import edu.dosw.rideci.exceptions.ExternalServiceException;

@RequiredArgsConstructor
@Repository
@Slf4j
public class GeolocalizationAdapter implements GeolocalizationRepositoryPort {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final MapsServicePort mapsServicePort;
    private final CalculateRouteWithWayPointsUseCase calculateRouteWithWayPointsUseCase;

    @Override
    public Route createRoute(CreateRouteCommand event) {
        System.out.println(event.getOrigin().getDirection());
        System.out.println(event.getDestiny().getDirection());

        Route googleData = mapsServicePort.calculateRoute(event.getOrigin(), event.getDestiny());

        Route route = Route.builder()
                .travelId(event.getTravelId())
                .origin(event.getOrigin())
                .destiny(event.getDestiny())
                .totalDistance(googleData.getTotalDistance())
                .estimatedTime(googleData.getEstimatedTime())
                .polyline(googleData.getPolyline())
                .departureDateAndTime(event.getDepartureDateAndTime())
                .pickUpPoints(null)
                .build();

        RouteDocument createdRoute = routeMapper.toDocument(route);
        RouteDocument savedRoute = routeRepository.save(createdRoute);

        return routeMapper.toDomain(savedRoute);
    }

    @Override
    public Route updateRoute(String routeId, Route newRoute){
        
        RouteDocument routeDoc = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException("Route not found with id: {id} "));
        Route route = routeMapper.toDomain(routeDoc);

        if(LocalDateTime.now().isAfter(route.getDepartureDateAndTime().minusMinutes(30))){
            throw new TimeOutException("Cannot edit after passed 30 minutes before the travel start");
        }

        boolean locationChanged = !route.getOrigin().equals(newRoute.getOrigin()) || !route.getDestiny().equals(newRoute.getDestiny());

        if(locationChanged){
            Route googleData = mapsServicePort.calculateRoute(newRoute.getOrigin(), newRoute.getDestiny());
            
            route.setOrigin(newRoute.getOrigin());
            route.setDestiny(newRoute.getDestiny());
            route.setTotalDistance(googleData.getTotalDistance()); 
            route.setEstimatedTime(googleData.getEstimatedTime());
            route.setPolyline(googleData.getPolyline());
        }

        route.setDepartureDateAndTime(newRoute.getDepartureDateAndTime());
        route.setPickUpPoints(newRoute.getPickUpPoints());

        RouteDocument updatedRoute = routeMapper.toDocument(route);
        routeRepository.save(updatedRoute);
            
        return route;
    }

    @Override
    public Location updateLocation(String routeId, Location newLocation){
        
        RouteDocument route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException("Route with id: {id} was not found"));

        LocationDocument actualLocation = route.getTravelTracking().getLastLocation();


        if (newLocation.getAccuracy() > 50.0){
            return routeMapper.toLocationDomain(actualLocation);
        }


        double remainingDistance = route.getTravelTracking().getRemainingDistance();
        if(remainingDistance < 50.0){
            //Implementar logica de notificacion
            
        }

        //double distanceToDest = geoCalculator.calculateDistanceInMeters(
          //      newLocation.getLatitude(),
            //    newLocation.getLongitude(),
              //  route.getDestinationLatitude(), 
                //route.getDestinationLongitude()
        //);

        //route.getTravelTracking().setRemainingDistance(distanceToDest);
        
        Location updatedLocation = Location.builder()
            .latitude(newLocation.getLatitude())
            .longitude(newLocation.getLongitude())
            .timeStamp(LocalDateTime.now())
            .speed(newLocation.getSpeed())
            .placeId(newLocation.getPlaceId())
            .direction(newLocation.getDirection())
            .accuracy(newLocation.getAccuracy())
            .build();

        LocationDocument updatedLocationDocument = routeMapper.toLocationDocumentEmbeddable(updatedLocation);

        route.getTravelTracking().getLocationHistory().add(actualLocation);
        route.getTravelTracking().setLastLocation(updatedLocationDocument);
        route.getTravelTracking().setLastUpdate(LocalDateTime.now());


        routeRepository.save(route);

        return updatedLocation;

    }

    @Override
    public PickUpPoint addPickUpPoint(String routeId, PickUpPoint newPickUpPoint){

        RouteDocument DocRoute = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException("Route with id: {id} was not found "));

        Route route = routeMapper.toDomain(DocRoute);

        if(LocalDateTime.now().isAfter(route.getDepartureDateAndTime().minusMinutes(30))){
            throw new TimeOutException("Cannot edit after passed 30 minutes before the travel start");
        }
                    
        PickUpPoint newPoint = PickUpPoint.builder()
            .passengerId(newPickUpPoint.getPassengerId())
            .distanceFromPreviousStop(newPickUpPoint.getDistanceFromPreviousStop()) 
            .passengerLocation(newPickUpPoint.getPassengerLocation()) 
            .estimatedTimeToPick(newPickUpPoint.getEstimatedTimeToPick())
            .order(newPickUpPoint.getOrder()) 
            .build();

        route.getPickUpPoints().add(newPoint);

        Route recalculatedRoute = calculateRouteWithWayPointsUseCase.calculateRouteWithWayPoints(route.getOrigin(), route.getDestiny(), route.getPickUpPoints());

        route.setTotalDistance(recalculatedRoute.getTotalDistance());
        route.setEstimatedTime(recalculatedRoute.getEstimatedTime());
        route.setPolyline(recalculatedRoute.getPolyline());

        routeRepository.save(routeMapper.toDocument(route));

        return newPoint;
    }

    @Override
    public PickUpPoint updatePickUpPoint(String routeId, PickUpPoint updatedPickUpPoint){

        RouteDocument DocRoute = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException("Route with id: {id} was not found"));

        Route route = routeMapper.toDomain(DocRoute);

        if(LocalDateTime.now().isAfter(route.getDepartureDateAndTime().minusMinutes(30))){
            throw new TimeOutException("Cannot edit after passed 30 minutes before the travel start");
        }

        Route recalculatedRoute = calculateRouteWithWayPointsUseCase.calculateRouteWithWayPoints(route.getOrigin(), route.getDestiny(), route.getPickUpPoints());
        
        //PickUpPoint pickUpPoint = route.getPickUpPoints()

        return null;
    
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


}
