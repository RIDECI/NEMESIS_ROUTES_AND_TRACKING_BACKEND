package edu.dosw.rideci.application.port.in;

public interface UpdateConfigurableIntervalUseCase {

    void updateIntervalSeconds(String routeId, int newInterval);

}
