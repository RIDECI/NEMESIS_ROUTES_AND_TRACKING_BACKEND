package edu.dosw.rideci.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Route {

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
