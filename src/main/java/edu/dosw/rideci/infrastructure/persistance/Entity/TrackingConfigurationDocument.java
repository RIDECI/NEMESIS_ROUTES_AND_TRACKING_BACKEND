package edu.dosw.rideci.infrastructure.persistance.Entity;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@Document(collection = "TrackingConfiguration")
public class TrackingConfigurationDocument {
    
    private int updateIntervalSeconds;

    private boolean shareLocationByDefault;

    private int maxTimeWithoutUpdate;
}
