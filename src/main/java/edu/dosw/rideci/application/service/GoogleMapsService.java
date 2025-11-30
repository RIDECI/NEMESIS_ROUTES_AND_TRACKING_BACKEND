package edu.dosw.rideci.application.service;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import edu.dosw.rideci.application.port.in.CalculateRouteWithWayPointsUseCase;
import edu.dosw.rideci.application.port.in.MapsServicePort;
import edu.dosw.rideci.application.port.in.RecalculateETA;
import edu.dosw.rideci.application.port.out.GoogleMapsRepositoryPort;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleMapsService implements MapsServicePort, RecalculateETA, CalculateRouteWithWayPointsUseCase {

    private final GoogleMapsRepositoryPort googleMapsRepositoryPort;

    @Override
    public Route calculateRoute(Location origin, Location destiny){
        return googleMapsRepositoryPort.calculateRoute(origin, destiny);
    }

    @Override
    public Long recalculateETA(Long newETA){
        return googleMapsRepositoryPort.recalculateETA(newETA);
    }

    @Override
    public Route calculateRouteWithWayPoints(Location origin, Location destiny, List<PickUpPoint> pickUpPoints){
        return googleMapsRepositoryPort.calculateRouteWithWayPoints(origin, destiny, pickUpPoints);
    }

}
