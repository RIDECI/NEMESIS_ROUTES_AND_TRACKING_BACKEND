package edu.dosw.rideci.infrastructure.controller.dto.Request;

import edu.dosw.rideci.domain.model.Location;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickUpPointRequest {
    
    private Long passengerId;

    private double distanceFromPreviousStop;

    private Location passengerLocation;

    private Long estimatedTimeToPick;

    private int order;
    
}
