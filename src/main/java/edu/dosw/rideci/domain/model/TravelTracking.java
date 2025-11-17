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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TravelTracking {

    private Long travelId;

    private Location lastLocation;

    private List<Location> locationHistory;

    private double distanceTraveled;

    private double remainingDistance;

    private LocalDateTime lastUpdate;

}
