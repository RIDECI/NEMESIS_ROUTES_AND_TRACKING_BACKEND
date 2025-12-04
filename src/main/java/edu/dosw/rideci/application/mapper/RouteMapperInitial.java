package edu.dosw.rideci.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.infrastructure.controller.dto.Request.RouteRequest;
import edu.dosw.rideci.infrastructure.controller.dto.Response.LocationResponse;
import edu.dosw.rideci.infrastructure.controller.dto.Response.PickUpPointResponse;
import edu.dosw.rideci.infrastructure.controller.dto.Response.RouteResponse;

@Mapper(componentModel = "spring")
public interface RouteMapperInitial {

    RouteResponse toResponse(Route route);

    Route toDomain(RouteRequest routeRequest);

    List<RouteResponse> toListResponse(List<Route> routes);

    List<Route> toListDomain(List<RouteRequest> routes);

    LocationResponse toLocationResponse(Location location);

    PickUpPointResponse toPickUpPointResponse(PickUpPoint pickUpPoint);

    List<PickUpPointResponse> toListPickUpPointsResponse(List<PickUpPoint> pickUpPoints);

}
