package edu.dosw.rideci.infrastructure.persistance.Entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickupPointDocument {

    private LocationDocument location;

    private Long passengerId;

    private LocalDateTime estimatedTimeToPick;

}
