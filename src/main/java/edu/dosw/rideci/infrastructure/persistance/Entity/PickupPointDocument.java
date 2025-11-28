package edu.dosw.rideci.infrastructure.persistance.Entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickupPointDocument {

    private Long passengerId;

    private double distanceFromPreviousStop;

    private LocationDocument passengerLocation;

    private LocalDateTime estimatedTimeToPick;

    private int order;

}
