package edu.dosw.rideci.application.port.out;

import java.util.List;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.Route;

public interface GoogleMapsRepositoryPort {

    Route calculateRoute(Location origin, Location destiny);

    Long recalculateETA(Long newETA);

    Route calculateRouteWithWayPoints(Location origin, Location destiny, List<PickUpPoint> pickUpPoints);

    boolean isPickUpLocationOnPath(double pickUpPointLat, double pickUpPointLon, String encodedPolyline, double toleranceMeters);
}
