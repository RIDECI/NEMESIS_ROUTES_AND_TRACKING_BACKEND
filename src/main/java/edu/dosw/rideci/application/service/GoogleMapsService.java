package edu.dosw.rideci.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.dosw.rideci.application.port.in.CalculateRouteWithWayPointsUseCase;
import edu.dosw.rideci.application.port.in.IsPickUpLocationOnPath;
import edu.dosw.rideci.application.port.in.MapsServicePort;
import edu.dosw.rideci.application.port.in.RecalculateETA;
import edu.dosw.rideci.application.port.in.ScheduledEtaRecalculation;
import edu.dosw.rideci.application.port.out.GoogleMapsRepositoryPort;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleMapsService implements MapsServicePort, RecalculateETA, CalculateRouteWithWayPointsUseCase,
     IsPickUpLocationOnPath, ScheduledEtaRecalculation{

    private final GoogleMapsRepositoryPort googleMapsRepositoryPort;

    @Override
    public Route calculateRoute(Location origin, Location destiny){
        return googleMapsRepositoryPort.calculateRoute(origin, destiny);
    }

    @Override
    public void recalculateETA(String routeId, Location lastLocation, Location destiny){
        googleMapsRepositoryPort.recalculateETA(routeId, lastLocation, destiny);
    }

    @Override
    public Route calculateRouteWithWayPoints(Location origin, Location destiny, List<PickUpPoint> pickUpPoints){
        return googleMapsRepositoryPort.calculateRouteWithWayPoints(origin, destiny, pickUpPoints);
    }

    @Override
    public boolean isPickUpLocationOnPath(double pickUpPointLat, double pickUpPointLon, String encodedPolyline, double toleranceMeters){
        return googleMapsRepositoryPort.isPickUpLocationOnPath(pickUpPointLat, pickUpPointLon, encodedPolyline, toleranceMeters);
    }

    @Override
    public void scheduledEtaRecalculation(){
        googleMapsRepositoryPort.scheduledEtaRecalculation();
    }

}
