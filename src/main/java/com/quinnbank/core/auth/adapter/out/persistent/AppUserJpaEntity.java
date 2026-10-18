package com.quinnbank.core.auth.adapter.out.persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "appuser")
public class AppUserJpaEntity {
    @Id
    UUID id;

    @NotNull
    @Column(name = "username" ,length = 100)
    String username;

    @NotNull
    @Column()
    String passwordHash;

    @Column(name = "firsttime_login_remaining")
    boolean firstTimeLoginRemaining;

    @Column(name = "nonexpired")
    boolean nonexpired;

    @Column(name = "nonlocked")
    boolean nonlocked;

    @Column(name = "nonexpired_credentials")
    boolean nonexpiredCredentials;

    @Column()
    boolean enabled;

    @NotNull
    @Column(name = "last_time_password_updated")
    LocalDate lastTimePasswordUpdated;

    @Column(name = "password_never_expires")
    boolean passwordNeverExpires;

    @Column(name = "is_self_service_user")
    boolean isSelfServiceUser;

    @Column(name = "cannot_change_password")
    boolean cannotChangePassword;

    @NotNull
    @Column(name = "created_at")
    LocalDate createdAt;

    @NotNull
    @Column(name = "updated_at")
    LocalDate updatedAt;
}
