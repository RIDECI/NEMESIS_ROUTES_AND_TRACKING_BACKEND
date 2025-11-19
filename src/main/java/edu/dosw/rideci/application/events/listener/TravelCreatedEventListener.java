package edu.dosw.rideci.application.events.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import edu.dosw.rideci.application.dto.TravelCreatedEvent;
import edu.dosw.rideci.application.port.in.CreateRouteUseCase;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TravelCreatedEventListener {

    private final CreateRouteUseCase createRouteUseCase;

    @RabbitListener(queues = "geolocation.travel.created.queue") // me toca poner este nombre en travel cuando publique
                                                                 // la cola
    public void handleTravelCreated(TravelCreatedEvent event) {
        createRouteUseCase.createRoute(event);
    }

}
