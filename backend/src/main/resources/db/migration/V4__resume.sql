create table resumes (
    id                 uuid primary key default gen_random_uuid(),
    user_id            uuid not null references users(id) on delete cascade,
    file_url           varchar(500) not null,
    original_filename  varchar(255) not null,
    is_active          boolean not null default true,
    parsed_at          timestamptz,
    created_at         timestamptz not null default now()
);
create index idx_resumes_user on resumes (user_id);

create table resume_skills (
    id          uuid primary key default gen_random_uuid(),
    resume_id   uuid not null references resumes(id) on delete cascade,
    skill_id    uuid references skills(id),
    raw_label   varchar(255) not null       -- as extracted, before mapping to canonical skill
);

create table resume_experiences (
    id             uuid primary key default gen_random_uuid(),
    resume_id      uuid not null references resumes(id) on delete cascade,
    company        varchar(255) not null,
    title          varchar(255) not null,
    start_date     date,
    end_date       date,
    description    text
);

create table resume_educations (
    id              uuid primary key default gen_random_uuid(),
    resume_id       uuid not null references resumes(id) on delete cascade,
    institution     varchar(255) not null,
    degree          varchar(255),
    field_of_study  varchar(255),
    start_date      date,
    end_date        date
);

create table resume_projects (
    id           uuid primary key default gen_random_uuid(),
    resume_id    uuid not null references resumes(id) on delete cascade,
    name         varchar(255) not null,
    description  text,
    tech_stack   varchar(500),
    url          varchar(500)
);

create table resume_certifications (
    id           uuid primary key default gen_random_uuid(),
    resume_id    uuid not null references resumes(id) on delete cascade,
    name         varchar(255) not null,
    issuer       varchar(255),
    issued_date  date
);

create table resume_analyses (
    id                       uuid primary key default gen_random_uuid(),
    resume_id                uuid not null unique references resumes(id) on delete cascade,
    resume_score             numeric(5,2) not null,
    ats_score                numeric(5,2) not null,
    missing_skills           jsonb not null default '[]',
    keyword_analysis         jsonb not null default '{}',
    grammar_suggestions      jsonb not null default '[]',
    improvement_suggestions  jsonb not null default '[]',
    raw_ai_response          jsonb,
    created_at               timestamptz not null default now()
);
