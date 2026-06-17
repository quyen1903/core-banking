package com.quinnbank.core.identity.api.query;

import com.quinnbank.core.identity.api.dto.IdentityUserResponse;
import com.quinnbank.core.identity.api.mapper.IdentityUserHttpMapper;
import com.quinnbank.core.identity.application.port.in.GetIdentityUserUseCase;
import com.quinnbank.core.identity.application.query.GetIdentityUserByPublicIdQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/identity/users")
@RequiredArgsConstructor
public class IdentityUserQueryController {

    private final GetIdentityUserUseCase getIdentityUserUseCase;
    private final IdentityUserHttpMapper mapper;

    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('IDENTITY_USER_VIEW')")
    public IdentityUserResponse getIdentityUser(@PathVariable UUID publicId) {
        return mapper.toResponse(getIdentityUserUseCase.getByPublicId(new GetIdentityUserByPublicIdQuery(publicId)));
    }
}
