create table user_profiles (
    user_id             uuid primary key references users(id) on delete cascade,
    full_name           varchar(255),
    headline            varchar(255),               -- e.g. "Backend Engineer, 3 YOE"
    avatar_url          varchar(500),
    experience_level    varchar(30),                -- INTERN | JUNIOR | MID | SENIOR | STAFF
    current_job_title   varchar(255),
    target_job_role_id  uuid references job_roles(id),
    github_url          varchar(500),
    linkedin_url        varchar(500),
    portfolio_url       varchar(500),
    bio                 text,
    is_public           boolean not null default false,
    updated_at          timestamptz not null default now()
);

create table user_preferences (
    user_id             uuid primary key references users(id) on delete cascade,
    theme               varchar(10) not null default 'SYSTEM', -- LIGHT | DARK | SYSTEM
    language            varchar(10) not null default 'en',
    email_notifications boolean not null default true,
    push_notifications  boolean not null default true,
    updated_at          timestamptz not null default now()
);
