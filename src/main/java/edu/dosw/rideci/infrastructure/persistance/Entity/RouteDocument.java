package edu.dosw.rideci.infrastructure.persistance.Entity;

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

    private LocationDocument destiny;

    private double totalDistance;

    private List<PickupPointDocument> pickupPoints;

}