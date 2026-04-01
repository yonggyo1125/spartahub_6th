package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.spartahub.common.exception.BadRequestException;
import org.spartahub.hubservice.domain.hub.service.AddressResolver;
import org.spartahub.hubservice.domain.hub.service.Coordinates;
import org.springframework.util.StringUtils;

@Embeddable
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HubLocation {

    @Column(name="hub_address", length=150, nullable=false)
    private String address;

    private double latitude; // 위도 - 가로선
    private double longitude; // 경로 - 세로선

    // 주소 정보는 필수 이며, 주소는 위도, 경도로 변환되어야 한다.
    // 주소 정보가 업데이트 되면 허브간 이동 경로 조합이 업데이트 되어야 한다.
    protected HubLocation(String address, AddressResolver resolver) {
        if (!StringUtils.hasText(address)) {
            throw new BadRequestException("주소는 필수입력 값 입니다.");
        }

        Coordinates coordinates = resolver.resolve(address);
        if (coordinates == null) {
            throw new BadRequestException("유효하지 않은 주소입니다: " + address);
        }

        this.address = address;
        this.latitude = coordinates.latitude(); // 위도
        this.longitude = coordinates.longitude(); // 경도
    }
}
