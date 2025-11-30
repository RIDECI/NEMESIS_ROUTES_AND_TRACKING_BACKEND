package edu.dosw.rideci.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.dosw.rideci.application.dto.TravelCreatedEvent;
import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.application.port.in.CreateRouteUseCase;
import edu.dosw.rideci.application.port.in.GetRouteInformationUseCase;
import edu.dosw.rideci.application.port.out.GeolocalizationRepositoryPort;
import edu.dosw.rideci.application.port.in.GetRealTimePositionUseCase;
import edu.dosw.rideci.application.port.in.GetPickUpPointsUseCase;
import edu.dosw.rideci.application.port.in.UpdateConfigurableIntervalUseCase;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.domain.model.TrackingConfiguration;
import edu.dosw.rideci.domain.model.Location;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeolocalizationService implements CreateRouteUseCase, GetRouteInformationUseCase,
        GetRealTimePositionUseCase, GetPickUpPointsUseCase, UpdateConfigurableIntervalUseCase {

    private final GeolocalizationRepositoryPort geolocalizationRepositoryPort;

    @Override
    public Route createRoute(CreateRouteCommand event) {
        return geolocalizationRepositoryPort.createRoute(event);
    }

    @Override
    public Route getRouteInformation(String routeId) {
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

    @Override
    public TrackingConfiguration updateIntervalSeconds(String routeId, int newInterval) {
        return geolocalizationRepositoryPort.updateIntervalSeconds(routeId, newInterval);
    }

}