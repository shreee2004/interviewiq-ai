create table learning_resources (
    id          uuid primary key default gen_random_uuid(),
    type        varchar(30) not null,   -- VIDEO | ARTICLE | COURSE | BOOK | LEETCODE | SYSTEM_DESIGN
    title       varchar(500) not null,
    url         varchar(500) not null,
    skill_id    uuid references skills(id),
    difficulty  varchar(20)
);

create table user_learning_recommendations (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references users(id) on delete cascade,
    resource_id     uuid not null references learning_resources(id) on delete cascade,
    reason          varchar(500),          -- why AI recommended it (skill-gap driven)
    completed       boolean not null default false,
    recommended_at  timestamptz not null default now()
);

create table notifications (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references users(id) on delete cascade,
    type        varchar(50) not null,
    title       varchar(255) not null,
    body        varchar(1000),
    read_at     timestamptz,
    created_at  timestamptz not null default now()
);
create index idx_notifications_user on notifications (user_id, read_at);
