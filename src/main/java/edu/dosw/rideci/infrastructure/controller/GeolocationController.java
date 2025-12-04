package edu.dosw.rideci.infrastructure.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.rideci.application.port.in.GetRouteInformationUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/geolocations")
@RequiredArgsConstructor
public class GeolocationController {

    private final GetRouteInformationUseCase getRouteInformationUseCase;

}
