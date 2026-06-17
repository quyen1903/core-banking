package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.application.port.out.IdentityAccountRepositoryPort;
import com.quinnbank.core.identity.domain.model.IdentityAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class IdentityAccountPersistenceAdapter implements IdentityAccountRepositoryPort {

    private final SpringDataIdentityAccountRepository identityAccountRepository;
    private final SpringDataCredentialRepository credentialRepository;
    private final IdentityAccountPersistenceMapper mapper = new IdentityAccountPersistenceMapper();

    @Override
    public IdentityAccount save(IdentityAccount identityAccount) {
        IdentityAccountJpaEntity savedAccount = identityAccountRepository.save(
                mapper.toIdentityAccountEntity(identityAccount)
        );
        CredentialJpaEntity savedCredential = credentialRepository.save(mapper.toCredentialEntity(identityAccount));
        return mapper.toDomain(savedAccount, savedCredential);
    }

    @Override
    public Optional<IdentityAccount> findById(UUID identityAccountId) {
        return identityAccountRepository.findById(identityAccountId)
                .flatMap(account -> credentialRepository.findByIdentityAccountIdAndActiveTrue(account.getId())
                        .map(credential -> mapper.toDomain(account, credential)));
    }

    @Override
    public boolean existsByUsername(String username) {
        return identityAccountRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return identityAccountRepository.existsByEmail(email);
    }
}
