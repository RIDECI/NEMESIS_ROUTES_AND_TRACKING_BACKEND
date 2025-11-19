package edu.dosw.rideci.domain.model;

import java.time.LocalDateTime;

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

    private Long travelId;

    private Location origin;

    private Location destiny;

    private double totalDistance;

    private LocalDateTime departureDateAndTime;

}
