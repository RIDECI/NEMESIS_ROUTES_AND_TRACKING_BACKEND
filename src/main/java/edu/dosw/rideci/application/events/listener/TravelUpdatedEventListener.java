package edu.dosw.rideci.application.events.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import edu.dosw.rideci.application.dto.TravelUpdatedEvent;
import edu.dosw.rideci.application.events.command.UpdateRouteCommand;
import edu.dosw.rideci.application.port.in.UpdateRouteUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TravelUpdatedEventListener {

    private final UpdateRouteUseCase updateRoute;

    @RabbitListener(queues = "geolocation.travel.updated.queue")
    public void handleTravelUpdated(TravelUpdatedEvent event) {

        UpdateRouteCommand command = new UpdateRouteCommand(
                event.getTravelId(),
                event.getOrigin(),
                event.getDestiny());

        try {
            updateRoute.updateRoute(command);
        } catch (Exception ex) {
            log.error("Failed to create route for travelId={}. Event will be logged for manual review.",
                    event.getTravelId(), ex);
        }

    }

}
