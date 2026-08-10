create table job_roles (
    id          uuid primary key default gen_random_uuid(),
    slug        varchar(100) not null unique,   -- java-backend, frontend, devops, ...
    name        varchar(255) not null,
    category    varchar(100)                    -- Engineering, Data, Security, ...
);

create table companies (
    id          uuid primary key default gen_random_uuid(),
    slug        varchar(100) not null unique,
    name        varchar(255) not null,
    logo_url    varchar(500)
);

create table skills (
    id          uuid primary key default gen_random_uuid(),
    slug        varchar(100) not null unique,
    name        varchar(255) not null,
    category    varchar(100)                    -- Language, Framework, Concept, Tool
);
