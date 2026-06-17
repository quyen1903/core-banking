package com.quinnbank.core.identity.application.port.out;

import com.quinnbank.core.identity.domain.model.IdentitySubjectType;

import java.util.UUID;

public interface IdentitySubjectLookupPort {

    void requireActiveSubject(IdentitySubjectType subjectType, UUID subjectId);
}
