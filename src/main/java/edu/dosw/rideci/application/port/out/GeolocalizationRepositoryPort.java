package edu.dosw.rideci.application.port.out;

import java.util.List;

import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.domain.model.TrackingConfiguration;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Location;


public interface GeolocalizationRepositoryPort {

    Route createRoute(CreateRouteCommand event);

    Route getRouteInformation(Long routeId);

    List<PickUpPoint> getPickUpPoints(Long routeId);

    Location getRealTimePosition(Long routeId);

    void updateIntervalSeconds(Long routeId, int newInterval);

    Location updateLocation(Long routeId, Location newLocation);

    Route updateRoute(Long routeId, Route newRoute);

    PickUpPoint addPickUpPoint(Long routeId, PickUpPoint newPickUpPoint);

    PickUpPoint updatePickUpPoint(Long routeId, PickUpPoint updatedPickUpPoint);
}
