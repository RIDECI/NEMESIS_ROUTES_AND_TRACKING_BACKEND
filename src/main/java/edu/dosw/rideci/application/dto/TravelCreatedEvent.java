package edu.dosw.rideci.application.dto;

import java.time.LocalDateTime;

import edu.dosw.rideci.domain.model.Location;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TravelCreatedEvent {

    private Long travelId;

    private Long driverId;

    private Location origin;

    private Location destiny;

    private LocalDateTime departureDateAndTime;

}