package org.spartahub.userservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.spartahub.common.exception.BadRequestException;
import org.springframework.util.StringUtils;

@Embeddable
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Contact {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    @Column(length=65, unique = true)
    private String email;

    @Column(length=45)
    private String slackId;

    protected Contact(String email, String slackId) {
        email = StringUtils.hasText(email) ? email : null;
        slackId = StringUtils.hasText(slackId) ? slackId : null;

        // 이메일 형식 체크
        if (StringUtils.hasText(email) && !email.matches(EMAIL_REGEX)) {
            throw new BadRequestException("유효하지 않은 이메일 형식입니다: " + email);
        }

        this.email = email;
        this.slackId = slackId;
    }
}
