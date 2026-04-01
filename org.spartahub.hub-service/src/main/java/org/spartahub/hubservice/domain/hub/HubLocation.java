package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HubLocation {

    @Column(length=150, nullable=false)
    private String address;

    private double latitude;
    private double longitude;

    
}
