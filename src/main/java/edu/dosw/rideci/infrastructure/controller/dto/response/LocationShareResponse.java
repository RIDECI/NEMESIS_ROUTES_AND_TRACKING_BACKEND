package edu.dosw.rideci.infrastructure.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import edu.dosw.rideci.domain.model.enums.ShareStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationShareResponse {

    private Long userId;

    private String travelId;

    private List<Long> emergencyContacts;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private String trackingUrl;

    private ShareStatus shareStatus;

}
