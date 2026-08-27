create table feature_flags (
    key         varchar(100) primary key,
    enabled     boolean not null default false,
    description varchar(500),
    updated_at  timestamptz not null default now()
);

create table api_usage_logs (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid references users(id),
    provider        varchar(30) not null,   -- OPENAI | GEMINI
    endpoint        varchar(255),
    tokens_input    int,
    tokens_output   int,
    cost_usd        numeric(10,4),
    latency_ms      int,
    created_at      timestamptz not null default now()
);
create index idx_api_usage_user on api_usage_logs (user_id, created_at);

create table system_logs (
    id          uuid primary key default gen_random_uuid(),
    level       varchar(10) not null,
    logger      varchar(255),
    message     text,
    metadata    jsonb,
    created_at  timestamptz not null default now()
);
