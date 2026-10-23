package com.quinnbank.core.auth.domain.model;

import java.util.Date;

import tools.jackson.databind.deser.jdk.UUIDDeserializer;

public final class  PasswordCredential {
    private UUIDDeserializer identityPublicId;
    private String passwordHash;
    private Date passwordChangedAt;
    private String mustChangePassword;
}
