package edu.dosw.rideci.infrastructure.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.DirectionsApi.Response;

import edu.dosw.rideci.application.port.in.AddPickUpPointUseCase;
import edu.dosw.rideci.application.port.in.DeleteRouteUseCase;
import edu.dosw.rideci.application.port.in.GetPickUpPointsUseCase;
import edu.dosw.rideci.application.port.in.GetRealTimePositionUseCase;
import edu.dosw.rideci.application.port.in.GetRouteInformationUseCase;
import edu.dosw.rideci.application.port.in.RemovePickUpPointUseCase;
import edu.dosw.rideci.application.port.in.UpdateConfigurableIntervalUseCase;
import edu.dosw.rideci.application.port.in.UpdateLocationUseCase;
import edu.dosw.rideci.application.port.in.UpdatePickUpPointUseCase;
import edu.dosw.rideci.application.port.in.UpdateRouteUseCase;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.infrastructure.controller.dto.Response.LocationResponse;
import edu.dosw.rideci.infrastructure.controller.dto.Response.PickUpPointResponse;
import edu.dosw.rideci.infrastructure.controller.dto.Response.RouteResponse;
import edu.dosw.rideci.infrastructure.persistance.mapper.RouteMapper;
import edu.dosw.rideci.application.mapper.RouteMapperInitial;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
@Tag(name= "Geolocalization, Routes & Tracking", description = "API for routes management, location share & travel tracking")
public class GeolocalizationController {

    private final UpdateRouteUseCase updateRouteUseCase;
    private final DeleteRouteUseCase deleteRouteUseCase;
    private final UpdateLocationUseCase updateLocationUseCase;
    private final AddPickUpPointUseCase addPickUpPointUseCase;
    private final UpdatePickUpPointUseCase updatePickUpPointUseCase;
    private final RemovePickUpPointUseCase removePickUpPointUseCase;
    private final GetRouteInformationUseCase getRouteInformationUseCase;
    private final GetPickUpPointsUseCase getPickUpPointsUseCase;
    private final GetRealTimePositionUseCase getRealTimePositionUseCase;
    private final UpdateConfigurableIntervalUseCase updateConfigurableIntervalUseCase;
    private final RouteMapperInitial routeMapper;


    @PutMapping("/{id}")
    public ResponseEntity<RouteResponse> updateRoute(
            @Parameter(description= "Route ID to be updated", required = true) @PathVariable String routeId,
            @Parameter(description= "New route data", required = true) @PathVariable Route newRoute
        ){

        RouteResponse updated = routeMapper.toResponse(updateRouteUseCase.updateRoute(routeId, newRoute));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(
            @Parameter(description= "Route ID to be deleted", required = true) @PathVariable String routeId){

        deleteRouteUseCase.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<LocationResponse> updateLocation(
            @Parameter(description = "Route ID to update location") @PathVariable String routeId,
            @Parameter(description = "New location data") @PathVariable Location newLocation){

        LocationResponse updated = routeMapper.toLocationResponse(updateLocationUseCase.updateLocation(routeId, newLocation));
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/pickuppoint")
    public ResponseEntity<PickUpPointResponse> addPickUpPoint(
            @Parameter(description = "Route ID to add pick up point") @PathVariable String routeId,
            @Parameter(description = "New pick up point on the route") @PathVariable PickUpPoint newPickUpPoint){
    
        PickUpPointResponse created = routeMapper.toPickUpPointResponse(addPickUpPointUseCase.addPickUpPoint(routeId, newPickUpPoint));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }

    @PutMapping("/{id}/pickuppoint")
    public ResponseEntity<PickUpPointResponse> updatePickUpPoint(
            @Parameter(description = "Route ID to update pick up point") @PathVariable String routeId,
            @Parameter(description = "New pick up point data") @PathVariable PickUpPoint updatedPickUpPoint){
        
        PickUpPointResponse updated = routeMapper.toPickUpPointResponse(updatePickUpPointUseCase.updatePickUpPoint(routeId, updatedPickUpPoint));
        return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/{id}/pickuppoint")
    public ResponseEntity<Void> removePickUpPoint(
            @Parameter(description= "Route ID to delete pick up point", required = true) @PathVariable String routeId,
            @Parameter(description = "Pick up point to be deleted") @PathVariable PickUpPoint pickUpPoint){

        removePickUpPointUseCase.removePickUpPoint(routeId, pickUpPoint);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> getRouteInformation(
            @Parameter(description= "Route ID to search", required = true) @PathVariable String routeId){

        RouteResponse route = routeMapper.toResponse(getRouteInformationUseCase.getRouteInformation(routeId));
        return ResponseEntity.ok(route);
    }

    @GetMapping("/{id}/pickuppoint")
    public ResponseEntity<List<PickUpPointResponse>> getPickUpPoints(
            @Parameter(description= "Route ID to search pick up points", required = true) @PathVariable String routeId){
        
        List<PickUpPointResponse> pickUpPoints = routeMapper.toListPickUpPointsResponse(getPickUpPointsUseCase.getPickUpPoints(routeId));
        return ResponseEntity.ok(pickUpPoints);
    }

    @GetMapping("/{id}/locationshare")
    public ResponseEntity<LocationResponse> getRealTimePOsition(
            @Parameter(description= "Route ID to search user real time position", required = true) @PathVariable String routeId){
        
        LocationResponse location = routeMapper.toLocationResponse(getRealTimePositionUseCase.getRealTimePosition(routeId));
        return ResponseEntity.ok(location);
    }

    @PutMapping("/{id}/trackingconfiguration")
    public ResponseEntity<Void> updateIntervalSeconds(
            @Parameter(description= "Route ID to update location interval seconds", required = true) @PathVariable String routeId,
            @Parameter(description = "New interval to be established", required = true) @PathVariable int newInterval ){

        updateConfigurableIntervalUseCase.updateIntervalSeconds(routeId, newInterval);
        return ResponseEntity.noContent().build();
    }


    
}
