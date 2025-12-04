package edu.dosw.rideci.infrastructure.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import edu.dosw.rideci.domain.model.Location;
import edu.dosw.rideci.domain.model.LocationShare;
import edu.dosw.rideci.domain.model.PickUpPoint;
import edu.dosw.rideci.domain.model.TravelTracking;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteResponse {

    private String id;

    private String travelId;

    private Location origin;

    private Location destiny;

    private double totalDistance;

    private Long estimatedTime;

    private String polyline;

    private LocalDateTime departureDateAndTime;

    private List<PickUpPoint> pickUpPoints;

    private LocationShare locationShare;

    private TravelTracking travelTracking;

}
