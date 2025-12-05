package edu.dosw.rideci.infrastructure.persistance.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.application.events.command.UpdateRouteCommand;
import edu.dosw.rideci.application.port.in.CalculateRouteWithWayPointsUseCase;
import edu.dosw.rideci.application.port.in.IsPickUpLocationOnPath;
import edu.dosw.rideci.application.port.in.MapsServicePort;
import edu.dosw.rideci.application.port.out.GeolocalizationRepositoryPort;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.domain.model.TravelTracking;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.LocationShare;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.infrastructure.persistance.Entity.LocationDocument;
import edu.dosw.rideci.infrastructure.persistance.Entity.RouteDocument;
import edu.dosw.rideci.infrastructure.persistance.mapper.RouteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import edu.dosw.rideci.exceptions.RouteNotFoundException;
import edu.dosw.rideci.exceptions.TimeOutException;
import edu.dosw.rideci.exceptions.InvalidPickUpPointException;

@RequiredArgsConstructor
@Repository
@Slf4j
public class GeolocalizationAdapter implements GeolocalizationRepositoryPort {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final MapsServicePort mapsServicePort;
    private final IsPickUpLocationOnPath isPickUpLocationOnPathUseCase;
    private final CalculateRouteWithWayPointsUseCase calculateRouteWithWayPointsUseCase;
    private final GeolocationUtils geolocationUtils;

    @Override
    public Route createRoute(CreateRouteCommand event) {

        Route googleData = mapsServicePort.calculateRoute(event.getOrigin(), event.getDestiny());

        Route route = Route.builder()
                .travelId(event.getTravelId())
                .origin(event.getOrigin())
                .destiny(event.getDestiny())
                .totalDistance(googleData.getTotalDistance())
                .estimatedTime(googleData.getEstimatedTime())
                .polyline(googleData.getPolyline())
                .departureDateAndTime(event.getDepartureDateAndTime())
                .pickUpPoints(new ArrayList<PickUpPoint>())
                .locationShare(new LocationShare()) //Mirar si no dan null
                .travelTracking(new TravelTracking()) //Mirar si no dan null
                .build();

        RouteDocument createdRoute = routeMapper.toDocument(route);
        RouteDocument savedRoute = routeRepository.save(createdRoute);

        return routeMapper.toDomain(savedRoute);
    }

    @Override
    public Route updateRoute(UpdateRouteCommand newRoute) {

        RouteDocument routeDoc = routeRepository.findByTravelId(newRoute.getTravelId());

        if (routeDoc == null) {
            throw new RouteNotFoundException("Route Not Found with travel id: " + newRoute.getTravelId());
        }

        Route route = routeMapper.toDomain(routeDoc);

        // if
        // (LocalDateTime.now().isAfter(route.getDepartureDateAndTime().minusMinutes(30)))
        // {
        // throw new TimeOutException("Cannot edit after passed 30 minutes before the
        // travel start");
        // }

        boolean locationChanged = !route.getOrigin().equals(newRoute.getOrigin())
                || !route.getDestiny().equals(newRoute.getDestiny());

        if (locationChanged) {
            Route googleData = mapsServicePort.calculateRoute(newRoute.getOrigin(), newRoute.getDestiny());

            route.setOrigin(newRoute.getOrigin());
            route.setDestiny(newRoute.getDestiny());
            route.setTotalDistance(googleData.getTotalDistance());
            route.setEstimatedTime(googleData.getEstimatedTime());
            route.setPolyline(googleData.getPolyline());
        }
        // route.setPickUpPoints(newRoute.getPickUpPoints()); serguio 

        RouteDocument updatedRoute = routeMapper.toDocument(route);
        routeRepository.save(updatedRoute);

        return route;
    }

    @Override
    public void removePickUpPoint(String routeId, PickUpPoint pickUpPoint) {
        RouteDocument routeDoc = routeRepository.findByTravelId(routeId);
        Route route = routeMapper.toDomain(routeDoc);

        if (routeDoc != null && route.getPickUpPoints() != null) {
            route.getPickUpPoints().removeIf(p -> p.getPassengerId().equals(pickUpPoint.getPassengerId()));
            routeRepository.save(routeDoc);
        }

        calculateRouteWithWayPointsUseCase.calculateRouteWithWayPoints(route.getOrigin(), route.getDestiny(), route.getPickUpPoints());
    }

    @Override
    public void deleteRoute(String travelId) {

        RouteDocument routeToDelete = routeRepository.findByTravelId(travelId);

        routeRepository.delete(routeToDelete);
    }

    @Override
    public Location updateLocation(String routeId, Location newLocation) {

        RouteDocument routeDoc = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route with id: {id} was not found"));

        Route route = routeMapper.toDomain(routeDoc);
        
        Location actualLocation = route.getTravelTracking().getLastLocation();

        if (newLocation.getAccuracy() > 50.0) {
            return actualLocation != null ? actualLocation : newLocation;
        }   

        Location updatedLocation = Location.builder()
                .latitude(newLocation.getLatitude())
                .longitude(newLocation.getLongitude())
                .timeStamp(LocalDateTime.now())
                .speed(newLocation.getSpeed())
                .placeId(newLocation.getPlaceId())
                .direction(newLocation.getDirection())
                .accuracy(newLocation.getAccuracy())
                .build();   
                
        if(isPickUpLocationOnPathUseCase.isPickUpLocationOnPath(updatedLocation.getLatitude()
            , updatedLocation.getLongitude(), routeId, 5000)){
            log.info("You've deviated more than 5km from the route");
            //Implementar notificacion
        }

        if (actualLocation != null) {
            route.getTravelTracking().getLocationHistory().add(actualLocation);
        }

        double distanceToDest = geolocationUtils.calculateDistanceInMeters(
            newLocation.getLatitude(),
            newLocation.getLongitude(),
            route.getDestiny().getLatitude(),
            route.getDestiny().getLongitude()
        );

        double distanceTraveled = geolocationUtils.calculateDistanceInMeters(
            route.getOrigin().getLatitude(), 
            route.getOrigin().getLongitude(), 
            newLocation.getLatitude(), 
            newLocation.getLongitude()
        );

        route.getTravelTracking().setRemainingDistance(distanceToDest);
        route.getTravelTracking().setLastLocation(updatedLocation);
        route.getTravelTracking().setLastUpdate(LocalDateTime.now());
        route.getTravelTracking().setDistanceTraveled(distanceTraveled);

        if (route.getTravelTracking().getRemainingDistance() < 50.0) {
            log.info("Driver is arriving. Distance: {} meters", route.getTravelTracking().getRemainingDistance());
        } 
      
        RouteDocument updatedRouteDoc = routeMapper.toDocument(route);
        routeRepository.save(updatedRouteDoc);

        return updatedLocation;                                                                                                                                                                                                            

    }

    @Override
    public PickUpPoint addPickUpPoint(String routeId, PickUpPoint newPickUpPoint) {

        RouteDocument DocRoute = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route with id: {id} was not found "));

        Route route = routeMapper.toDomain(DocRoute);

        if (LocalDateTime.now().isAfter(route.getDepartureDateAndTime().minusMinutes(30))) {
            throw new TimeOutException("Cannot edit after passed 30 minutes before the travel start");
        }

        PickUpPoint newPoint = PickUpPoint.builder()
                .passengerId(newPickUpPoint.getPassengerId())
                .distanceFromPreviousStop(newPickUpPoint.getDistanceFromPreviousStop())
                .passengerLocation(newPickUpPoint.getPassengerLocation())
                .estimatedTimeToPick(newPickUpPoint.getEstimatedTimeToPick())
                .order(newPickUpPoint.getOrder())
                .build();

        boolean isPickUpLocationOnPath = isPickUpLocationOnPathUseCase.isPickUpLocationOnPath(
                newPickUpPoint.getPassengerLocation().getLatitude(),
                newPickUpPoint.getPassengerLocation().getLongitude(), route.getPolyline(), 100.0);

        if (!isPickUpLocationOnPath) {
            throw new InvalidPickUpPointException("The pick up point choosed is not valid for the route");
        }

        route.getPickUpPoints().add(newPoint);

        Route recalculatedRoute = calculateRouteWithWayPointsUseCase.calculateRouteWithWayPoints(route.getOrigin(),
                route.getDestiny(), route.getPickUpPoints());

        route.setTotalDistance(recalculatedRoute.getTotalDistance());
        route.setEstimatedTime(recalculatedRoute.getEstimatedTime());
        route.setPolyline(recalculatedRoute.getPolyline());

        routeRepository.save(routeMapper.toDocument(route));

        return newPickUpPoint;
    }

    @Override
    public PickUpPoint updatePickUpPoint(String routeId, PickUpPoint updatedPickUpPoint) {

        RouteDocument DocRoute = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route with id: {id} was not found"));

        Route route = routeMapper.toDomain(DocRoute);

        if (LocalDateTime.now().isAfter(route.getDepartureDateAndTime().minusMinutes(30))) {
            throw new TimeOutException("Cannot edit after passed 30 minutes before the travel start");
        }

        PickUpPoint pickUpPoint = route.getPickUpPoints().stream()
                .filter(p -> p.getPassengerId().equals(updatedPickUpPoint.getPassengerId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "PickUpPoint for passenger " + updatedPickUpPoint.getPassengerId() + " not found"));

        pickUpPoint.setPassengerLocation(updatedPickUpPoint.getPassengerLocation());
        pickUpPoint.setDistanceFromPreviousStop(updatedPickUpPoint.getDistanceFromPreviousStop());
        pickUpPoint.setEstimatedTimeToPick(updatedPickUpPoint.getEstimatedTimeToPick());

        //Route recalculatedRoute = calculateRouteWithWayPointsUseCase.calculateRouteWithWayPoints(route.getOrigin(),
        //        route.getDestiny(), route.getPickUpPoints());

        // PickUpPoint pickUpPoint = route.getPickUpPoints()
        // serguio
        return null;

    }

    //public String generateLocationShareLink(String routeId, Long userId ){
    //    return null;
    //}
    
    @Override
    public Route getRouteInformation(String travelId) {

        RouteDocument route = routeRepository.findByTravelId(travelId);

        if (route == null) {
            throw new RouteNotFoundException("Route not found with travelId: " + travelId);
        }

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
