package edu.dosw.rideci.application.service;

import org.springframework.stereotype.Service;

import edu.dosw.rideci.application.dto.TravelCreatedEvent;
import edu.dosw.rideci.application.port.in.CreateRouteUseCase;
import edu.dosw.rideci.application.port.out.GeolocalizationRepositoryPort;
import edu.dosw.rideci.domain.model.Route;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeolocalizationService implements CreateRouteUseCase {

    private final GeolocalizationRepositoryPort geolocalizationRepositoryPort;

    @Override
    public Route createRoute(TravelCreatedEvent event) {
        return geolocalizationRepositoryPort.createRoute(event);
    }

}
