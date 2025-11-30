package edu.dosw.rideci.application.port.in;

import java.util.List;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;
import edu.dosw.rideci.infrastructure.persistance.Entity.LocationDocument;

public interface CalculateRouteWithWayPointsUseCase {

    Route calculateRouteWithWayPoints(Location origin, Location destination, List<PickUpPoint> pickUpPoints);

}
