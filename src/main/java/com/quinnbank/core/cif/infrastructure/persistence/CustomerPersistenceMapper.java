package com.quinnbank.core.cif.infrastructure.persistence;

import com.quinnbank.core.cif.domain.model.Customer;

final class CustomerPersistenceMapper {
    private CustomerPersistenceMapper() {}

    static CustomerJpaEntity toNewEntity(Customer customer) {
        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.id = customer.getId();
        entity.customerNumber = customer.getCustomerNumber();
        entity.fullName = customer.getFullName();
        entity.firstName = customer.getFirstName();
        entity.lastName = customer.getLastName();
        entity.officeId = customer.getOfficeId();
        entity.externalId = customer.getExternalId();
        entity.email = customer.getEmail();
        entity.phone = customer.getPhone();
        entity.status = customer.getStatus().name();
        entity.kycStatus = customer.getKycStatus().name();
        entity.riskRating = customer.getRiskRating().name();
        entity.createdAt = customer.getCreatedAt();
        entity.updatedAt = customer.getUpdatedAt();
        // Null version tells Spring Data this assigned-UUID entity is new; Hibernate initializes it.
        return entity;
    }
}
