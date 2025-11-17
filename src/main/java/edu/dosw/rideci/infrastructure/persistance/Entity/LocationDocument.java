package edu.dosw.rideci.infrastructure.persistance.Entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDocument {

    private double longitude;

    private double latitude;

}
