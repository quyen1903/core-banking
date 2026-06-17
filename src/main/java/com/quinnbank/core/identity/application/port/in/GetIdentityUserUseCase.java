package com.quinnbank.core.identity.application.port.in;

import com.quinnbank.core.identity.application.query.GetIdentityUserByPublicIdQuery;
import com.quinnbank.core.identity.application.result.IdentityUserSnapshot;

public interface GetIdentityUserUseCase {

    IdentityUserSnapshot getByPublicId(GetIdentityUserByPublicIdQuery query);
}
