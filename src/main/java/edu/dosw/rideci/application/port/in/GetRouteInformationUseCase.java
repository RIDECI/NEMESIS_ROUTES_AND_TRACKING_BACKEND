package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.Route;

public interface GetRouteInformationUseCase {
    
    Route getRouteInformation(Long routeId);

}
