package edu.dosw.rideci.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingConfiguration {
    
    private int updateIntervalSeconds;

    private boolean shareLocationByDefault;

    private int maxTimeWithoutUpdate;
}
