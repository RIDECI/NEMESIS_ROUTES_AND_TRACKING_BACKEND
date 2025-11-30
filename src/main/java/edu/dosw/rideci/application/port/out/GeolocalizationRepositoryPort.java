package edu.dosw.rideci.application.port.out;

import java.util.List;

import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Location;

public interface GeolocalizationRepositoryPort {

    Route createRoute(CreateRouteCommand event);

    Route getRouteInformation(String routeId);

    List<PickUpPoint> getPickUpPoints(String routeId);

    Location getRealTimePosition(String routeId);

    void updateIntervalSeconds(String routeId, int newInterval);

    Location updateLocation(String routeId, Location newLocation);

    Route updateRoute(String routeId, Route newRoute);

    PickUpPoint addPickUpPoint(String routeId, PickUpPoint newPickUpPoint);

    PickUpPoint updatePickUpPoint(String routeId, PickUpPoint updatedPickUpPoint);
}
