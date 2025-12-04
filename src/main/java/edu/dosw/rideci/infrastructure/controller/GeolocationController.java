package edu.dosw.rideci.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.rideci.application.mapper.RouteMapperInitial;
import edu.dosw.rideci.application.port.in.GetRouteInformationUseCase;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.infrastructure.controller.dto.response.RouteResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/geolocations")
@RequiredArgsConstructor
public class GeolocationController {

    private final GetRouteInformationUseCase getRouteInformationUseCase;
    private final RouteMapperInitial routeMapperInitial;

    @GetMapping("/{travelId}")
    public ResponseEntity<RouteResponse> getRouteInformation(
            @PathVariable String travelId) {
        Route routeInformation = getRouteInformationUseCase.getRouteInformation(travelId);

        return ResponseEntity.ok(routeMapperInitial.toResponse(routeInformation));
    }

}
