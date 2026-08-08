create extension if not exists pgcrypto;

create table users (
    id                  uuid primary key default gen_random_uuid(),
    email               varchar(255) not null unique,
    password_hash       varchar(255),              -- null for OAuth-only accounts
    email_verified      boolean not null default false,
    role                varchar(20) not null default 'USER', -- USER | ADMIN
    status              varchar(20) not null default 'ACTIVE', -- ACTIVE | SUSPENDED | DELETED
    two_factor_enabled  boolean not null default false,
    two_factor_secret   varchar(255),
    created_at          timestamptz not null default now(),
    updated_at          timestamptz not null default now(),
    created_by          uuid,
    updated_by          uuid
);
create index idx_users_email on users (email);

create table oauth_accounts (
    id                  uuid primary key default gen_random_uuid(),
    user_id             uuid not null references users(id) on delete cascade,
    provider            varchar(30) not null,        -- GOOGLE
    provider_user_id    varchar(255) not null,
    created_at          timestamptz not null default now(),
    unique (provider, provider_user_id)
);
create index idx_oauth_user on oauth_accounts (user_id);

create table refresh_tokens (
    id                  uuid primary key default gen_random_uuid(),
    user_id             uuid not null references users(id) on delete cascade,
    token_hash          varchar(255) not null unique,
    device_label        varchar(255),
    ip_address          varchar(45),
    expires_at          timestamptz not null,
    revoked_at          timestamptz,
    created_at          timestamptz not null default now()
);
create index idx_refresh_user on refresh_tokens (user_id);

create table email_verification_tokens (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references users(id) on delete cascade,
    token_hash  varchar(255) not null unique,
    expires_at  timestamptz not null,
    consumed_at timestamptz,
    created_at  timestamptz not null default now()
);

create table password_reset_tokens (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references users(id) on delete cascade,
    token_hash  varchar(255) not null unique,
    expires_at  timestamptz not null,
    consumed_at timestamptz,
    created_at  timestamptz not null default now()
);
