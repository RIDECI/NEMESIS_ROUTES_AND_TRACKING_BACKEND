package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.TrackingConfiguration;

public interface UpdateConfigurableIntervalUseCase {

    TrackingConfiguration updateIntervalSeconds(String routeId, int newInterval);

}
