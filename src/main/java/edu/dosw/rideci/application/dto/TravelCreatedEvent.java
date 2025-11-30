package edu.dosw.rideci.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import edu.dosw.rideci.domain.model.Location;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TravelCreatedEvent {

    private String travelId;

    private Long driverId;

    private Location origin;

    private Location destiny;

    private List<Long> passengersId;

    private LocalDateTime departureDateAndTime;

}