package edu.dosw.rideci.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import edu.dosw.rideci.domain.model.enums.ShareStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationShare {
    
    private Long id;

    private Long userId;

    private Long travelId;

    private List<String> emergencyContacts;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private String trackingUrl;

    private ShareStatus shareStatus;
}
