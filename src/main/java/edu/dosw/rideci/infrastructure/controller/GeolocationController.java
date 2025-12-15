package edu.dosw.rideci.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.rideci.application.mapper.RouteMapperInitial;
import edu.dosw.rideci.application.port.in.GetRouteInformationUseCase;
import edu.dosw.rideci.application.port.in.UpdateLocationUseCase;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.infrastructure.controller.dto.response.LocationResponse;
import edu.dosw.rideci.infrastructure.controller.dto.response.RouteResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/geolocations")
@RequiredArgsConstructor
@Tag(name = "Geolocation, Routes and Tracking", description = "API for Routes and Tracking Management in RIDECI")
public class GeolocationController {

    private final GetRouteInformationUseCase getRouteInformationUseCase;
    private final UpdateLocationUseCase updateLocationUseCase;
    private final RouteMapperInitial routeMapperInitial;

    @GetMapping("/{travelId}")
    public ResponseEntity<RouteResponse> getRouteInformation(
            @Parameter(description = "ID of the travel to get the information", required = true) @PathVariable String travelId) {
        Route routeInformation = getRouteInformationUseCase.getRouteInformation(travelId);

        return ResponseEntity.ok(routeMapperInitial.toResponse(routeInformation));
    }

    @PutMapping("/{travelId}/travelTracking/location")
    public ResponseEntity<LocationResponse> updateLocation(
            @Parameter(description = "ID of the travel", required = true) @PathVariable String travelId,
            @Parameter(description = "New location of the user", required = true) @RequestBody Location newLocation){

        LocationResponse locationUpdated = routeMapperInitial.toLocationResponse(updateLocationUseCase.updateLocation(travelId, newLocation));

        return ResponseEntity.ok(locationUpdated);
    }

}
