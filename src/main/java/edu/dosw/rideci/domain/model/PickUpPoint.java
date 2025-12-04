package edu.dosw.rideci.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PickUpPoint {

    private Long passengerId;

    private double distanceFromPreviousStop;

    private Location passengerLocation;

    private Long estimatedTimeToPick;

    private int order;
}
