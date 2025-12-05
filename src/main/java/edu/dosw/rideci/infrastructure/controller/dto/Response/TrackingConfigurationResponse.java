package edu.dosw.rideci.infrastructure.controller.dto.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackingConfigurationResponse {
    
    private int updateIntervalSeconds;

    private boolean shareLocationByDefault;

    private int maxTimeWithoutUpdate;
    
}
