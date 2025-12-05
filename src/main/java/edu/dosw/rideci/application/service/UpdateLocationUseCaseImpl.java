package edu.dosw.rideci.application.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import edu.dosw.rideci.application.events.LocationUpdateEvent;
import edu.dosw.rideci.application.port.in.UpdateLocationUseCase;
import edu.dosw.rideci.application.port.out.GeolocalizationRepositoryPort;
import edu.dosw.rideci.domain.model.Location;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateLocationUseCaseImpl implements UpdateLocationUseCase{

    private final GeolocalizationRepositoryPort geolocalizationRepositoryPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Location updateLocation(String routeId, Location newLocation){

        Location updatedLocation = geolocalizationRepositoryPort.updateLocation(routeId, newLocation);
        eventPublisher.publishEvent(new LocationUpdateEvent(newLocation, routeId, updatedLocation));

        return updatedLocation;
    }

    
}
