package edu.dosw.rideci.application.events.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import edu.dosw.rideci.application.dto.TravelCreatedEvent;
import edu.dosw.rideci.application.events.command.CreateRouteCommand;
import edu.dosw.rideci.application.port.in.CreateRouteUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TravelCreatedEventListener {

    private final CreateRouteUseCase createRouteUseCase;

    @RabbitListener(queues = "geolocation.travel.created.queue")
    public void handleTravelCreated(TravelCreatedEvent event) {
        System.out.println("!!! MENSAJE RECIBIDO EN GEOLOCALIZACIÓN !!!");
        System.out.println("Datos del viaje: " + event.toString());
        System.out.println(event.getOrigin().getDirection());
        System.out.println(event.getOrigin().getLatitude());
        System.out.println(event.getOrigin().getLongitude());
 
        CreateRouteCommand command = new CreateRouteCommand(
                event.getTravelId(),
                event.getOrigin(),
                event.getDestiny(),
                event.getDepartureDateAndTime());
        try {
            createRouteUseCase.createRoute(command);
        } catch (Exception ex) {
            log.error("Failed to create route for travelId={}. Event will be logged for manual review.", event.getTravelId(), ex);
        }
    }

}