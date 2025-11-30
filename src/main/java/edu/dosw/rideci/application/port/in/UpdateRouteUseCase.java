package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.Route;

public interface UpdateRouteUseCase {
    
    Route updateRoute(String routeId, Route newRoute);
     
}
