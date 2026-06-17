package com.quinnbank.core.identity.application;

import com.quinnbank.core.identity.application.command.AssignIdentityRoleCommand;
import com.quinnbank.core.identity.application.command.ChangeIdentityUserStatusCommand;
import com.quinnbank.core.identity.application.command.CreateIdentityUserCommand;
import com.quinnbank.core.identity.application.command.RemoveIdentityRoleCommand;
import com.quinnbank.core.identity.application.command.SetIdentityPasswordCommand;
import com.quinnbank.core.identity.application.port.out.IdentityCredentialRepositoryPort;
import com.quinnbank.core.identity.application.port.out.IdentityUserRepositoryPort;
import com.quinnbank.core.identity.application.port.out.RoleRepositoryPort;
import com.quinnbank.core.identity.application.port.out.UserRoleRepositoryPort;
import com.quinnbank.core.identity.application.query.GetIdentityUserByPublicIdQuery;
import com.quinnbank.core.identity.application.result.IdentityUserSnapshot;
import com.quinnbank.core.identity.application.service.AssignIdentityRoleService;
import com.quinnbank.core.identity.application.service.ChangeIdentityUserStatusService;
import com.quinnbank.core.identity.application.service.CreateIdentityUserService;
import com.quinnbank.core.identity.application.service.GetIdentityUserService;
import com.quinnbank.core.identity.application.service.RemoveIdentityRoleService;
import com.quinnbank.core.identity.application.service.SetIdentityPasswordService;
import com.quinnbank.core.identity.domain.model.IdentityCredential;
import com.quinnbank.core.identity.domain.model.IdentityOwnerType;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import com.quinnbank.core.identity.domain.model.IdentityUserStatus;
import com.quinnbank.core.identity.domain.model.Role;
import com.quinnbank.core.identity.domain.model.RoleCode;
import com.quinnbank.core.identity.domain.model.UserRole;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IdentityUserUseCaseTest {

    private final IdentityUserRepositoryPort identityUserRepository = mock(IdentityUserRepositoryPort.class);
    private final IdentityCredentialRepositoryPort credentialRepository = mock(IdentityCredentialRepositoryPort.class);
    private final RoleRepositoryPort roleRepository = mock(RoleRepositoryPort.class);
    private final UserRoleRepositoryPort userRoleRepository = mock(UserRoleRepositoryPort.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-06-18T03:00:00Z"), ZoneOffset.UTC);

    private final CreateIdentityUserService createService = new CreateIdentityUserService(
            identityUserRepository,
            clock
    );
    private final GetIdentityUserService getService = new GetIdentityUserService(
            identityUserRepository,
            userRoleRepository
    );
    private final ChangeIdentityUserStatusService changeStatusService = new ChangeIdentityUserStatusService(
            identityUserRepository,
            userRoleRepository,
            clock
    );
    private final SetIdentityPasswordService setPasswordService = new SetIdentityPasswordService(
            identityUserRepository,
            credentialRepository,
            passwordEncoder,
            clock
    );
    private final AssignIdentityRoleService assignRoleService = new AssignIdentityRoleService(
            identityUserRepository,
            roleRepository,
            userRoleRepository,
            clock
    );
    private final RemoveIdentityRoleService removeRoleService = new RemoveIdentityRoleService(
            identityUserRepository,
            roleRepository,
            userRoleRepository
    );

    @Test
    void createIdentityUserSuccessfully() {
        when(identityUserRepository.existsByUsername("quyen.nguyen")).thenReturn(false);
        when(identityUserRepository.existsByEmail("quyen@example.invalid")).thenReturn(false);
        when(identityUserRepository.existsByPhoneNumber("+84901234567")).thenReturn(false);
        when(identityUserRepository.save(any(IdentityUser.class))).thenAnswer(invocation -> persist(invocation.getArgument(0)));

        IdentityUserSnapshot user = createService.create(new CreateIdentityUserCommand(
                IdentityOwnerType.CUSTOMER,
                1001L,
                "QUYEN.NGUYEN",
                "QUYEN@example.invalid",
                "+84901234567"
        ));

        assertThat(user.publicId()).isNotNull();
        assertThat(user.ownerType()).isEqualTo(IdentityOwnerType.CUSTOMER);
        assertThat(user.ownerId()).isEqualTo(1001L);
        assertThat(user.username()).isEqualTo("quyen.nguyen");
        assertThat(user.email()).isEqualTo("quyen@example.invalid");
        assertThat(user.phoneNumber()).isEqualTo("+84901234567");
        assertThat(user.status()).isEqualTo(IdentityUserStatus.PENDING_ACTIVATION);
        assertThat(user.roles()).isEmpty();
        verify(identityUserRepository).save(any(IdentityUser.class));
    }

    @Test
    void duplicateUsernameIsRejected() {
        when(identityUserRepository.existsByUsername("quyen.nguyen")).thenReturn(true);

        assertThatThrownBy(() -> createService.create(new CreateIdentityUserCommand(
                IdentityOwnerType.CUSTOMER,
                1001L,
                "quyen.nguyen",
                null,
                null
        )))
                .isInstanceOf(DuplicateIdentityUsernameException.class)
                .hasMessage("identity username already exists");

        verify(identityUserRepository, never()).save(any(IdentityUser.class));
    }

    @Test
    void getUserByPublicIdReturnsAssignedRoles() {
        IdentityUser user = persistedUser();
        when(identityUserRepository.findByPublicId(user.publicId())).thenReturn(Optional.of(user));
        when(userRoleRepository.findRoleCodesByIdentityUserId(user.id())).thenReturn(Set.of(RoleCode.CUSTOMER));

        IdentityUserSnapshot result = getService.getByPublicId(new GetIdentityUserByPublicIdQuery(user.publicId()));

        assertThat(result.publicId()).isEqualTo(user.publicId());
        assertThat(result.roles()).containsExactly(RoleCode.CUSTOMER);
    }

    @Test
    void updateStatus() {
        IdentityUser user = persistedUser();
        when(identityUserRepository.findByPublicId(user.publicId())).thenReturn(Optional.of(user));
        when(identityUserRepository.save(any(IdentityUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRoleRepository.findRoleCodesByIdentityUserId(user.id())).thenReturn(Set.of());

        IdentityUserSnapshot changed = changeStatusService.changeStatus(new ChangeIdentityUserStatusCommand(
                user.publicId(),
                IdentityUserStatus.ACTIVE
        ));

        assertThat(changed.status()).isEqualTo(IdentityUserStatus.ACTIVE);
        verify(identityUserRepository).save(any(IdentityUser.class));
    }

    @Test
    void setPasswordStoresEncodedHashNotRawPassword() {
        IdentityUser user = persistedUser();
        when(identityUserRepository.findByPublicId(user.publicId())).thenReturn(Optional.of(user));
        when(credentialRepository.findPasswordCredentialByIdentityUserId(user.id())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("StrongPassword123!")).thenReturn("{bcrypt}encoded-local-only");
        when(credentialRepository.save(any(IdentityCredential.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<IdentityCredential> credentialCaptor = ArgumentCaptor.forClass(IdentityCredential.class);

        setPasswordService.setPassword(new SetIdentityPasswordCommand(
                user.publicId(),
                "StrongPassword123!",
                false
        ));

        verify(credentialRepository).save(credentialCaptor.capture());
        assertThat(credentialCaptor.getValue().passwordHash()).isEqualTo("{bcrypt}encoded-local-only");
        assertThat(credentialCaptor.getValue().passwordHash()).isNotEqualTo("StrongPassword123!");
        assertThat(credentialCaptor.getValue().mustChangePassword()).isFalse();
    }

    @Test
    void assignRole() {
        IdentityUser user = persistedUser();
        Role role = Role.restore(1L, RoleCode.CUSTOMER);
        when(identityUserRepository.findByPublicId(user.publicId())).thenReturn(Optional.of(user));
        when(roleRepository.findByCode(RoleCode.CUSTOMER)).thenReturn(Optional.of(role));
        when(userRoleRepository.existsByIdentityUserIdAndRoleId(user.id(), role.id())).thenReturn(false);
        when(userRoleRepository.save(any(UserRole.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assignRoleService.assignRole(new AssignIdentityRoleCommand(user.publicId(), RoleCode.CUSTOMER));

        verify(userRoleRepository).save(any(UserRole.class));
    }

    @Test
    void duplicateRoleAssignmentDoesNotCreateDuplicateRelationship() {
        IdentityUser user = persistedUser();
        Role role = Role.restore(1L, RoleCode.CUSTOMER);
        when(identityUserRepository.findByPublicId(user.publicId())).thenReturn(Optional.of(user));
        when(roleRepository.findByCode(RoleCode.CUSTOMER)).thenReturn(Optional.of(role));
        when(userRoleRepository.existsByIdentityUserIdAndRoleId(user.id(), role.id())).thenReturn(true);

        assignRoleService.assignRole(new AssignIdentityRoleCommand(user.publicId(), RoleCode.CUSTOMER));

        verify(userRoleRepository, never()).save(any(UserRole.class));
    }

    @Test
    void removeRole() {
        IdentityUser user = persistedUser();
        Role role = Role.restore(1L, RoleCode.CUSTOMER);
        when(identityUserRepository.findByPublicId(user.publicId())).thenReturn(Optional.of(user));
        when(roleRepository.findByCode(RoleCode.CUSTOMER)).thenReturn(Optional.of(role));

        removeRoleService.removeRole(new RemoveIdentityRoleCommand(user.publicId(), RoleCode.CUSTOMER));

        verify(userRoleRepository).remove(user.id(), role.id());
    }

    private static IdentityUser persistedUser() {
        return IdentityUser.restore(
                42L,
                UUID.randomUUID(),
                IdentityOwnerType.CUSTOMER,
                1001L,
                "quyen.nguyen",
                "quyen@example.invalid",
                "+84901234567",
                IdentityUserStatus.PENDING_ACTIVATION,
                LocalDateTime.of(2026, 6, 18, 10, 0),
                LocalDateTime.of(2026, 6, 18, 10, 0),
                0
        );
    }

    private static IdentityUser persist(IdentityUser user) {
        return IdentityUser.restore(
                42L,
                user.publicId(),
                user.ownerType(),
                user.ownerId(),
                user.username(),
                user.email(),
                user.phoneNumber(),
                user.status(),
                user.createdAt(),
                user.updatedAt(),
                user.version()
        );
    }
}
