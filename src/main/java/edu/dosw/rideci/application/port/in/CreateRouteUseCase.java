package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.domain.model.Route;

public interface CreateRouteUseCase {

    Route createRoute(CreateRouteCommand event);

}
