package edu.dosw.rideci.infrastructure.persistance.Entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "travelTrackings")
public class TravelTrackingDocument {

    private Long travelId;

    private LocationDocument lastLocation;

    private double distanceTraveled;

    private double remainingDistance;

    private LocalDateTime lastUpdate;

}