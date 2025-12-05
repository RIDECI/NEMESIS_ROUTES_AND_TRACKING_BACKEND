package edu.dosw.rideci.application.events;

import org.springframework.context.ApplicationEvent;

import edu.dosw.rideci.domain.model.Location;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LocationUpdateEvent extends ApplicationEvent{

    private final String routeId;
    private final Location updatedLocation;
    
    public LocationUpdateEvent(Object source, String routeId, Location updatedLocation){
        super(source);
        this.routeId = routeId;
        this.updatedLocation = updatedLocation;
    }
}
