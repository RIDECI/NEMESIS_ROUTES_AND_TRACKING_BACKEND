package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.Location;

public interface GetRealTimePositionUseCase {

    Location getRealTimePosition(String routeId);

}
