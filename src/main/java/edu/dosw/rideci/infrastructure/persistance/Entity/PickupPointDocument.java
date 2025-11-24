package edu.dosw.rideci.infrastructure.persistance.Entity;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickupPointDocument {

    private Long passengerId;

    private double distanceFromPreviousStop;

    private LocationDocument passengerLocation;

    private int order;

}
