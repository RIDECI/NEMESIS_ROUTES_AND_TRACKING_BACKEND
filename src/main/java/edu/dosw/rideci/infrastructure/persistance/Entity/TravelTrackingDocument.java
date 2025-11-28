package edu.dosw.rideci.infrastructure.persistance.Entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "travelTrackings")
public class TravelTrackingDocument {

    private Long travelId;

    private LocationDocument lastLocation;

    private LocalDateTime lastUpdate;

    private List<LocationDocument> locationHistory;

    private double distanceTraveled;

    private double remainingDistance;

    private TrackingConfigurationDocument trackingConfiguration;
}