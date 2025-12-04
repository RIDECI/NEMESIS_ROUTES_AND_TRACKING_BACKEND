package edu.dosw.rideci.infrastructure.controller.dto.Request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationRequest {

    private double latitude;

    private double longitude;

    private LocalDateTime timeStamp;

    private double speed;

    private String placeId;

    private String direction;

    private double accuracy;
    
}
