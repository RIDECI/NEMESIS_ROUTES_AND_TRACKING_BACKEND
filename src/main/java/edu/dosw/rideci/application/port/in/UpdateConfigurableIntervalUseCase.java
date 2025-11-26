package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.TrackingConfiguration;

public interface UpdateConfigurableIntervalUseCase {
    
    TrackingConfiguration updateIntervalSeconds(Long routeId, int newInterval);

}
