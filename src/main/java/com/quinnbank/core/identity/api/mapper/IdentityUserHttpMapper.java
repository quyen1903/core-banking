package com.quinnbank.core.identity.api.mapper;

import com.quinnbank.core.identity.api.dto.AssignIdentityRoleRequest;
import com.quinnbank.core.identity.api.dto.ChangeIdentityUserStatusRequest;
import com.quinnbank.core.identity.api.dto.CreateIdentityUserRequest;
import com.quinnbank.core.identity.api.dto.IdentityUserResponse;
import com.quinnbank.core.identity.api.dto.SetIdentityPasswordRequest;
import com.quinnbank.core.identity.application.command.AssignIdentityRoleCommand;
import com.quinnbank.core.identity.application.command.ChangeIdentityUserStatusCommand;
import com.quinnbank.core.identity.application.command.CreateIdentityUserCommand;
import com.quinnbank.core.identity.application.command.RemoveIdentityRoleCommand;
import com.quinnbank.core.identity.application.command.SetIdentityPasswordCommand;
import com.quinnbank.core.identity.application.result.IdentityUserSnapshot;
import com.quinnbank.core.identity.domain.model.RoleCode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdentityUserHttpMapper {

    public CreateIdentityUserCommand toCommand(CreateIdentityUserRequest request) {
        return new CreateIdentityUserCommand(
                request.ownerType(),
                request.ownerId(),
                request.username(),
                request.email(),
                request.phoneNumber()
        );
    }

    public ChangeIdentityUserStatusCommand toCommand(UUID publicId, ChangeIdentityUserStatusRequest request) {
        return new ChangeIdentityUserStatusCommand(publicId, request.status());
    }

    public SetIdentityPasswordCommand toCommand(UUID publicId, SetIdentityPasswordRequest request) {
        return new SetIdentityPasswordCommand(
                publicId,
                request.newPassword(),
                request.mustChangePassword()
        );
    }

    public AssignIdentityRoleCommand toCommand(UUID publicId, AssignIdentityRoleRequest request) {
        return new AssignIdentityRoleCommand(publicId, request.roleCode());
    }

    public RemoveIdentityRoleCommand toRemoveRoleCommand(UUID publicId, RoleCode roleCode) {
        return new RemoveIdentityRoleCommand(publicId, roleCode);
    }

    public IdentityUserResponse toResponse(IdentityUserSnapshot user) {
        return IdentityUserResponse.from(user);
    }
}
