package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.PickUpPoint;

public interface AddPickUpPointUseCase {

    PickUpPoint addPickUpPoint(Long routeId, PickUpPoint newPickUpPoint);

}
