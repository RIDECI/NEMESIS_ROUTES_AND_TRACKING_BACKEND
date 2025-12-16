package edu.dosw.rideci.infrastructure.controller.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackingConfigurationRequest {

    private int updateIntervalSeconds;

    private boolean shareLocationByDefault;

    private int maxTimeWithoutUpdate;

}
