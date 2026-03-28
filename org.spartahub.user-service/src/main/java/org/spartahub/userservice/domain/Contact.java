package org.spartahub.userservice.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
record Contact(
        String email,
        String slackId
) implements Serializable {}
