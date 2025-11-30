package edu.dosw.rideci.application.events.command;

import java.time.LocalDateTime;

import edu.dosw.rideci.domain.model.Location;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRouteCommand {

    private String travelId;

    private Location origin;

    private Location destiny;

    private LocalDateTime departureDateAndTime;

}
