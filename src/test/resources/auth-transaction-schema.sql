-- Test fixture only. ${schema} is replaced with a fresh, internally generated identifier.
-- Every object is qualified so a pooled connection's search_path cannot select app tables.
create schema ${schema};

create table ${schema}.identity_user (
    id uuid primary key,
    public_id uuid not null,
    username varchar(100) not null,
    username_normalized varchar(100) collate "C" not null,
    owner_type varchar(16) not null,
    owner_id uuid not null,
    status varchar(32) not null,
    security_version bigint not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    version bigint not null,
    constraint uq_identity_user_public_id unique (public_id),
    constraint uq_identity_user_username unique (username_normalized),
    constraint ck_identity_user_username check (length(btrim(username)) > 0),
    constraint ck_identity_user_username_normalized check (
        username_normalized ~ '^[a-z0-9._@+-]+$'
        and username_normalized = lower(btrim(username) collate "C")
    ),
    constraint ck_identity_user_security_version check (security_version >= 0),
    constraint ck_identity_user_version check (version >= 0)
);

create index ix_identity_user_owner on ${schema}.identity_user (owner_type, owner_id);

create table ${schema}.identity_credential (
    user_id uuid primary key,
    password_hash varchar(512) not null,
    password_changed_at timestamp with time zone not null,
    must_change_password boolean not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    version bigint not null,
    constraint fk_identity_credential_user foreign key (user_id) references ${schema}.identity_user (id),
    constraint ck_identity_credential_hash check (length(btrim(password_hash)) > 0),
    constraint ck_identity_credential_version check (version >= 0)
);
