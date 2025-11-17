package edu.dosw.rideci.infrastructure.persistance.Entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "companionTracking")
public class CompanionTrackingDocument {

    @Id
    private Long companionId;

    private Long travelId;

    private LocationDocument location;

    private LocalDateTime timeStamp;

}
