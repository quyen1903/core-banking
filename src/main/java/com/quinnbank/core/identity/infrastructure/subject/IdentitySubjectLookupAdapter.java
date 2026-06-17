package com.quinnbank.core.identity.infrastructure.subject;

import com.quinnbank.core.cif.application.CustomerDirectory;
import com.quinnbank.core.employee.application.EmployeeDirectory;
import com.quinnbank.core.identity.application.port.out.IdentitySubjectLookupPort;
import com.quinnbank.core.identity.domain.exception.IdentityAccountCreationRejectedException;
import com.quinnbank.core.identity.domain.model.IdentitySubjectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class IdentitySubjectLookupAdapter implements IdentitySubjectLookupPort {

    private final CustomerDirectory customerDirectory;
    private final EmployeeDirectory employeeDirectory;

    @Override
    public void requireActiveSubject(IdentitySubjectType subjectType, UUID subjectId) {
        if (subjectType == IdentitySubjectType.CUSTOMER) {
            customerDirectory.requireActiveCustomer(subjectId);
            return;
        }
        if (subjectType == IdentitySubjectType.EMPLOYEE) {
            employeeDirectory.requireActiveEmployee(subjectId);
            return;
        }
        if (subjectType == IdentitySubjectType.SERVICE_ACCOUNT || subjectType == IdentitySubjectType.SYSTEM) {
            if (subjectId != null) {
                throw new IdentityAccountCreationRejectedException("service and system identities must not reference employee or customer subjects");
            }
            return;
        }

        throw new IdentityAccountCreationRejectedException("identity subject type is required");
    }
}
