package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.PickUpPoint;

public interface RemovePickUpPointUseCase {
    
    void removePickUpPoint(String routeId, PickUpPoint pickUpPoint);

}
