package edu.dosw.rideci.infrastructure.controller.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.TrackingConfiguration;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TravelTrackingRequest {

    private String travelId;

    private Location lastLocation;

    private LocalDateTime lastUpdate;

    private List<Location> locationHistory;

    private double distanceTraveled;

    private double remainingDistance;

    private TrackingConfiguration trackingConfiguration;
}
