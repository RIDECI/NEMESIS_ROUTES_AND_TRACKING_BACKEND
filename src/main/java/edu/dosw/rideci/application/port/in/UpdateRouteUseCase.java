package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.application.events.command.UpdateRouteCommand;
import edu.dosw.rideci.domain.model.Route;

public interface UpdateRouteUseCase {

    Route updateRoute(UpdateRouteCommand newRoute);

}
