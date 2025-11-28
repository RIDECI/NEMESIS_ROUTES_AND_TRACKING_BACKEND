package edu.dosw.rideci.application.service;

import org.springframework.stereotype.Repository;

import edu.dosw.rideci.application.port.in.MapsServicePort;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.infrastructure.persistance.repository.GoogleMapsAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class GoogleMapsService implements MapsServicePort {

    private final GoogleMapsAdapter googleMapsAdapter;

    @Override
    public Route calculateRoute(Location origin, Location destination){
        return googleMapsAdapter.calculateRoute(origin, destination);
    }

    
}
