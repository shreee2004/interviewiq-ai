create table user_skill_scores (
    user_id     uuid not null references users(id) on delete cascade,
    skill_id    uuid not null references skills(id) on delete cascade,
    score       numeric(5,2) not null default 0,
    sample_size int not null default 0,          -- number of evaluations contributing
    updated_at  timestamptz not null default now(),
    primary key (user_id, skill_id)
);

create table user_streaks (
    user_id             uuid primary key references users(id) on delete cascade,
    current_streak_days int not null default 0,
    longest_streak_days int not null default 0,
    last_activity_date  date,
    updated_at          timestamptz not null default now()
);

create table achievements (
    id          uuid primary key default gen_random_uuid(),
    slug        varchar(100) not null unique,
    name        varchar(255) not null,
    description varchar(500),
    icon        varchar(100),
    xp_reward   int not null default 0
);

create table user_achievements (
    user_id        uuid not null references users(id) on delete cascade,
    achievement_id uuid not null references achievements(id) on delete cascade,
    unlocked_at    timestamptz not null default now(),
    primary key (user_id, achievement_id)
);

create table user_xp (
    user_id     uuid primary key references users(id) on delete cascade,
    total_xp    bigint not null default 0,
    level       int not null default 1,
    updated_at  timestamptz not null default now()
);
