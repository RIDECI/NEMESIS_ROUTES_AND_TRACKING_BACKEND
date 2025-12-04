package edu.dosw.rideci.infrastructure.controller.dto.response;

import edu.dosw.rideci.domain.model.Location;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickUpPointResponse {

    private Long passengerId;

    private double distanceFromPreviousStop;

    private Location passengerLocation;

    private Long estimatedTimeToPick;

    private int order;

}
