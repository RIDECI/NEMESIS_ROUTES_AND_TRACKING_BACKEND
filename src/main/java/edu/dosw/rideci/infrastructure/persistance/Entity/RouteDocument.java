package edu.dosw.rideci.infrastructure.persistance.Entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "routes")
public class RouteDocument {

    private Long travelId;

    private LocationDocument origin;

    private LocationDocument destination;

    private double totalDistance;

    private int estimatedTime;

    private String polyline;

    private LocalDateTime departureDateAndTime;

    private List<PickupPointDocument> pickUpPoints;

    private LocationShareDocument locationShare;

    private TravelTrackingDocument travelTracking;

}