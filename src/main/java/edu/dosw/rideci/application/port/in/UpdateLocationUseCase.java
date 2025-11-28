package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.Location;

public interface UpdateLocationUseCase {

    Location updateLocation(Long routeId, Location newLocation);
    
}
