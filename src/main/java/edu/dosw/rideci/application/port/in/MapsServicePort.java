package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.Route;

public interface MapsServicePort {

    Route calculateRoute(Location origin, Location destination);

}
