package edu.dosw.rideci.infrastructure.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.TrackingConfiguration;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TravelTrackingResponse {

    private String travelId;

    private Location lastLocation;

    private LocalDateTime lastUpdate;

    private List<Location> locationHistory;

    private double distanceTraveled;

    private double remainingDistance;

    private TrackingConfiguration trackingConfiguration;

}
