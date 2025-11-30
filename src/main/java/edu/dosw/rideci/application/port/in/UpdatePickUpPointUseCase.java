package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.PickUpPoint;

public interface UpdatePickUpPointUseCase {
    
    PickUpPoint updatePickUpPoint(String routeId, PickUpPoint updatedPickUpPoint);

}
