create table interview_sessions (
    id                        uuid primary key default gen_random_uuid(),
    user_id                   uuid not null references users(id) on delete cascade,
    resume_id                 uuid references resumes(id),
    job_role_id               uuid references job_roles(id),
    company_id                uuid references companies(id),
    interview_type            varchar(30) not null,   -- TECHNICAL | CODING | SYSTEM_DESIGN | HR | MIXED
    difficulty                varchar(20) not null,   -- EASY | MEDIUM | HARD
    language                  varchar(30),            -- programming language, if applicable
    planned_duration_minutes  int not null,
    status                    varchar(20) not null default 'PENDING', -- PENDING | IN_PROGRESS | COMPLETED | ABANDONED
    started_at                timestamptz,
    completed_at              timestamptz,
    created_at                timestamptz not null default now()
);
create index idx_sessions_user on interview_sessions (user_id);
create index idx_sessions_status on interview_sessions (status);

create table interview_turns (
    id                 uuid primary key default gen_random_uuid(),
    session_id         uuid not null references interview_sessions(id) on delete cascade,
    sequence_no        int not null,
    question_text      text not null,
    question_topic     varchar(255),
    answer_text        text,
    answer_mode        varchar(10),            -- VOICE | TEXT
    response_time_ms   int,
    filler_word_count  int,
    speaking_wpm       numeric(6,2),
    asked_at           timestamptz not null default now(),
    answered_at        timestamptz,
    unique (session_id, sequence_no)
);
create index idx_turns_session on interview_turns (session_id);

create table answer_evaluations (
    id                     uuid primary key default gen_random_uuid(),
    turn_id                uuid not null unique references interview_turns(id) on delete cascade,
    technical_accuracy     numeric(5,2),
    confidence_score       numeric(5,2),
    communication_score    numeric(5,2),
    problem_solving_score  numeric(5,2),
    depth_score            numeric(5,2),
    clarity_score          numeric(5,2),
    grammar_score          numeric(5,2),
    fluency_score          numeric(5,2),
    overall_score          numeric(5,2) not null,
    feedback               text,
    strengths              jsonb not null default '[]',
    weaknesses             jsonb not null default '[]',
    improvement_tips       jsonb not null default '[]',
    raw_ai_response        jsonb,
    created_at             timestamptz not null default now()
);
