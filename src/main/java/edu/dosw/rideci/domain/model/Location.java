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
public class Location {

    private double latitude;

    private double longitude;

    private LocalDateTime timeStamp;

    private double speed;

    private String placeId;

    private String address;

    private double accuracy;

}
