package edu.dosw.rideci.application.port.in;

import java.util.List;

import edu.dosw.rideci.domain.model.PickUpPoint;

public interface GetPickUpPointsUseCase {

    List<PickUpPoint> getPickUpPoints(String routeId);

}
