package edu.dosw.rideci.infrastructure.persistance.Entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.dosw.rideci.domain.model.enums.ShareStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "locationShare")
public class LocationShareDocument {

    @Id
    private Long id;

    private Long userId;

    private Long travelId;

    private List<String> emergencyContacts;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private String trackingUrl;

    private ShareStatus shareStatus;

}
