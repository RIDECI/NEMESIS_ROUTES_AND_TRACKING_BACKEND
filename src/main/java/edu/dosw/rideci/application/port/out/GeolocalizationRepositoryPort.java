package edu.dosw.rideci.application.port.out;

import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.domain.model.Route;

public interface GeolocalizationRepositoryPort {

    Route createRoute(CreateRouteCommand event);

}
