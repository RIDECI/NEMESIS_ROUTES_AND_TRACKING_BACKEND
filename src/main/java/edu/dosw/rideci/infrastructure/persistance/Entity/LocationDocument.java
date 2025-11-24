package edu.dosw.rideci.infrastructure.persistance.Entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDocument {

    private double latitude;
    
    private double longitude;

    private double altitude;

    private LocalDateTime timeStamp;

    private double speed;

    private String placeId;

    private String address;

    private double accuracy;

}
