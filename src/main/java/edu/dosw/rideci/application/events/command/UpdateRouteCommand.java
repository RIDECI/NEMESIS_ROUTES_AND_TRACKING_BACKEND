package edu.dosw.rideci.application.events.command;

import edu.dosw.rideci.domain.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRouteCommand {

    private String travelId;

    private Location origin;

    private Location destiny;

}
