package edu.dosw.rideci.application.port.out;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.Route;

public interface GoogleMapsRepositoryPort {

    Route calculateRoute(Location origin, Location destination);

}
