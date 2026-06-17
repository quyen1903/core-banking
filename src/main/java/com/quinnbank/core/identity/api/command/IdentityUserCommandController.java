package com.quinnbank.core.identity.api.command;

import com.quinnbank.core.identity.api.dto.AssignIdentityRoleRequest;
import com.quinnbank.core.identity.api.dto.ChangeIdentityUserStatusRequest;
import com.quinnbank.core.identity.api.dto.CreateIdentityUserRequest;
import com.quinnbank.core.identity.api.dto.IdentityUserResponse;
import com.quinnbank.core.identity.api.dto.SetIdentityPasswordRequest;
import com.quinnbank.core.identity.api.mapper.IdentityUserHttpMapper;
import com.quinnbank.core.identity.application.port.in.AssignIdentityRoleUseCase;
import com.quinnbank.core.identity.application.port.in.ChangeIdentityUserStatusUseCase;
import com.quinnbank.core.identity.application.port.in.CreateIdentityUserUseCase;
import com.quinnbank.core.identity.application.port.in.RemoveIdentityRoleUseCase;
import com.quinnbank.core.identity.application.port.in.SetIdentityPasswordUseCase;
import com.quinnbank.core.identity.domain.model.RoleCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/identity/users")
@RequiredArgsConstructor
@Validated
public class IdentityUserCommandController {

    private final CreateIdentityUserUseCase createIdentityUserUseCase;
    private final ChangeIdentityUserStatusUseCase changeIdentityUserStatusUseCase;
    private final SetIdentityPasswordUseCase setIdentityPasswordUseCase;
    private final AssignIdentityRoleUseCase assignIdentityRoleUseCase;
    private final RemoveIdentityRoleUseCase removeIdentityRoleUseCase;
    private final IdentityUserHttpMapper mapper;

    @PostMapping
    @PreAuthorize("hasAuthority('IDENTITY_USER_CREATE')")
    public IdentityUserResponse createIdentityUser(@Valid @RequestBody CreateIdentityUserRequest request) {
        return mapper.toResponse(createIdentityUserUseCase.create(mapper.toCommand(request)));
    }

    @PatchMapping("/{publicId}/status")
    @PreAuthorize("hasAuthority('IDENTITY_USER_STATUS_CHANGE')")
    public IdentityUserResponse changeStatus(
            @PathVariable UUID publicId,
            @Valid @RequestBody ChangeIdentityUserStatusRequest request
    ) {
        return mapper.toResponse(changeIdentityUserStatusUseCase.changeStatus(mapper.toCommand(publicId, request)));
    }

    @PostMapping("/{publicId}/credentials/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('IDENTITY_USER_STATUS_CHANGE')")
    public void setPassword(
            @PathVariable UUID publicId,
            @Valid @RequestBody SetIdentityPasswordRequest request
    ) {
        setIdentityPasswordUseCase.setPassword(mapper.toCommand(publicId, request));
    }

    @PostMapping("/{publicId}/roles")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('IDENTITY_ROLE_ASSIGN')")
    public void assignRole(
            @PathVariable UUID publicId,
            @Valid @RequestBody AssignIdentityRoleRequest request
    ) {
        assignIdentityRoleUseCase.assignRole(mapper.toCommand(publicId, request));
    }

    @DeleteMapping("/{publicId}/roles/{roleCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('IDENTITY_ROLE_ASSIGN')")
    public void removeRole(@PathVariable UUID publicId, @PathVariable RoleCode roleCode) {
        removeIdentityRoleUseCase.removeRole(mapper.toRemoveRoleCommand(publicId, roleCode));
    }
}
