package edu.dosw.rideci.infrastructure.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteResponse {

    private String id;

    private String travelId;

    private LocationResponse origin;

    private LocationResponse destiny;

    private double totalDistance;

    private Long estimatedTime;

    private String polyline;

    private LocalDateTime departureDateAndTime;

    private List<PickUpPointResponse> pickUpPoints;

}
