package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.TrackingConfiguration;

public interface UpdateConfigurableIntervalUseCase {
    
    void updateIntervalSeconds(Long routeId, int newInterval);

}
