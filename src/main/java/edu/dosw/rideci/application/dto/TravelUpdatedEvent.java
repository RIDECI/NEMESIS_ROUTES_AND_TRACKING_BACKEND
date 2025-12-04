package edu.dosw.rideci.application.dto;

import edu.dosw.rideci.domain.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TravelUpdatedEvent {

    private String travelId;

    private Location origin;

    private Location destiny;

}
