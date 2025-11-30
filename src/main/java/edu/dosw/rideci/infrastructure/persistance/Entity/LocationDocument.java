package edu.dosw.rideci.infrastructure.persistance.Entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDocument {

    private double latitude;

    private double longitude;

    private LocalDateTime timeStamp;

    private double speed;

    private String placeId;

    private String direction;

    private double accuracy;

}
