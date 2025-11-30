package edu.dosw.rideci.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.dosw.rideci.application.dto.TravelCreatedEvent;
import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.application.port.in.AddPickUpPointUseCase;
import edu.dosw.rideci.application.port.in.CreateRouteUseCase;
import edu.dosw.rideci.application.port.in.GetRouteInformationUseCase;
import edu.dosw.rideci.application.port.in.MapsServicePort;
import edu.dosw.rideci.application.port.out.GeolocalizationRepositoryPort;
import edu.dosw.rideci.application.port.in.GetRealTimePositionUseCase;
import edu.dosw.rideci.application.port.in.GetPickUpPointsUseCase;
import edu.dosw.rideci.application.port.in.UpdateConfigurableIntervalUseCase;
import edu.dosw.rideci.application.port.in.UpdateLocationUseCase;
import edu.dosw.rideci.application.port.in.UpdatePickUpPointUseCase;
import edu.dosw.rideci.application.port.in.UpdateRouteUseCase;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.domain.model.Location;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeolocalizationService implements CreateRouteUseCase, GetRouteInformationUseCase,
        GetRealTimePositionUseCase, GetPickUpPointsUseCase, UpdateConfigurableIntervalUseCase,
        UpdateLocationUseCase, UpdateRouteUseCase, AddPickUpPointUseCase, UpdatePickUpPointUseCase {

    private final GeolocalizationRepositoryPort geolocalizationRepositoryPort;

    @Override
    public Route createRoute(CreateRouteCommand event) {
        return geolocalizationRepositoryPort.createRoute(event);
    }

    @Override
    public Route updateRoute(String routeId, Route newRoute){
        return geolocalizationRepositoryPort.updateRoute(routeId, newRoute);
    }

    @Override
    public Route getRouteInformation(String routeId){
        return geolocalizationRepositoryPort.getRouteInformation(routeId);
    }

    @Override
    public List<PickUpPoint> getPickUpPoints(String routeId) {
        return geolocalizationRepositoryPort.getPickUpPoints(routeId);
    }

    @Override
    public Location getRealTimePosition(String routeId) {
        return geolocalizationRepositoryPort.getRealTimePosition(routeId);
    }

    public void updateIntervalSeconds(String routeId, int newInterval) {
        geolocalizationRepositoryPort.updateIntervalSeconds(routeId, newInterval);
    }

    @Override
    public Location updateLocation(String routeId, Location newLocation) {
        return geolocalizationRepositoryPort.updateLocation(routeId, newLocation);
    }

    @Override 
    public PickUpPoint addPickUpPoint(String routeId, PickUpPoint newPickUpPoint){
        return geolocalizationRepositoryPort.addPickUpPoint(routeId, newPickUpPoint);
    }

    @Override
    public PickUpPoint updatePickUpPoint(String routeId, PickUpPoint updatedPickUpPoint){
        return geolocalizationRepositoryPort.updatePickUpPoint(routeId, updatedPickUpPoint);
    }

}