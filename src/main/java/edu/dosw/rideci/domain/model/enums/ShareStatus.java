package edu.dosw.rideci.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShareStatus {

    ACTIVE,
    EXPIRED,
    PENDING,
    ENDED,
    CANCELLED

}
