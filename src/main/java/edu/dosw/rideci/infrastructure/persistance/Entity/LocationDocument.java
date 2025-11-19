package edu.dosw.rideci.infrastructure.persistance.Entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDocument {

    private double longitude;

    private double latitude;

    private LocalDateTime timeStamp;

}
