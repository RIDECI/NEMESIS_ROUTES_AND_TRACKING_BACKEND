package edu.dosw.rideci.infrastructure.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.rideci.application.port.in.MapsServicePort;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.Route;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test")
public class TestGoogleMapsController {

    private final MapsServicePort mapsServicePort;

    @GetMapping("/google-maps")
    public Route testGoogleMaps() {
        Location origin = Location.builder()
                .address("Bogota, Colombia")
                .build();

        Location destination = Location.builder()
                .address("Medellin, Colombia")
                .build();

        return mapsServicePort.calculateRoute(origin, destination);
    }
}
