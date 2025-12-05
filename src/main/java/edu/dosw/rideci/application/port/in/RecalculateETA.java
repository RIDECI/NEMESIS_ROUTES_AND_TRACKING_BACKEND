package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.Location;

public interface RecalculateETA {

    void recalculateETA(String routeId, Location lastLocation, Location destiny);

}
