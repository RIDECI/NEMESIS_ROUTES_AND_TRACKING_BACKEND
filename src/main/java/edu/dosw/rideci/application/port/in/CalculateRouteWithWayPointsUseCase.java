package edu.dosw.rideci.application.port.in;

import java.util.List;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;

public interface CalculateRouteWithWayPointsUseCase {

    Route calculateRouteWithWayPoints(Location origin, Location destiny, List<PickUpPoint> pickUpPoints);

}
