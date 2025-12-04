package edu.dosw.rideci.application.events.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import edu.dosw.rideci.application.dto.TravelCancelledEvent;
import edu.dosw.rideci.application.events.command.DeleteRouteCommand;
import edu.dosw.rideci.application.port.in.DeleteRouteUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TravelCancelledEventListener {

    private final DeleteRouteUseCase deleteRouteUseCase;

    @RabbitListener(queues = "geolocation.travel.cancelled.queue")
    public void handleTravelCancelled(TravelCancelledEvent event) {

        DeleteRouteCommand command = new DeleteRouteCommand(
                event.getTravelId());

        try {
            deleteRouteUseCase.deleteRoute(command.getTravelId());
        } catch (Exception ex) {
            log.error("Failed to delete route for travelId={}. Event will be logged for manual review.",
                    event.getTravelId(), ex);
        }

    }

}
