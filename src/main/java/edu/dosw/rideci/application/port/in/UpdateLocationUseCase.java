package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.Location;

public interface UpdateLocationUseCase {

    Location updateLocation(String routeId, Location newLocation);

}
