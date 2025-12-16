package edu.dosw.rideci.infrastructure.controller.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationResponse {

    private double latitude;

    private double longitude;

    private double speed;

    private String placeId;

    private String direction;

    private double accuracy;

}
