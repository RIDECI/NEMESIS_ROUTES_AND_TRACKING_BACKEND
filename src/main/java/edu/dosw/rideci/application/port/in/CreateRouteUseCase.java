package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.application.dto.TravelCreatedEvent;
import edu.dosw.rideci.domain.model.Route;

public interface CreateRouteUseCase {

    Route createRoute(TravelCreatedEvent event);

}
