package edu.dosw.rideci.infrastructure.service;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import edu.dosw.rideci.application.events.LocationUpdateEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebSocketNotifier {
    
    private final SimpMessagingTemplate messagingTemplate;


    @EventListener
    public void handleLocationUpdate(LocationUpdateEvent event){
        
        String destination = "/topic/route/" + event.getRouteId() + "/location";

        messagingTemplate.convertAndSend(destination, event.getUpdatedLocation());
    }
}
