-- Starter reference data so interview session setup (GET /interviews/roles,
-- GET /interviews/companies) has something real to return. Not part of the reviewed
-- DATABASE.md schema doc itself — just seed content for the tables it already defines.

insert into job_roles (slug, name, category) values
    ('java-backend', 'Java Backend Engineer', 'Engineering'),
    ('frontend', 'Frontend Engineer', 'Engineering'),
    ('fullstack', 'Full-Stack Engineer', 'Engineering'),
    ('devops', 'DevOps Engineer', 'Engineering'),
    ('data-engineer', 'Data Engineer', 'Data'),
    ('data-scientist', 'Data Scientist', 'Data'),
    ('mobile-android', 'Android Engineer', 'Engineering'),
    ('mobile-ios', 'iOS Engineer', 'Engineering'),
    ('security-engineer', 'Security Engineer', 'Security'),
    ('ml-engineer', 'Machine Learning Engineer', 'Data');

insert into companies (slug, name) values
    ('google', 'Google'),
    ('amazon', 'Amazon'),
    ('microsoft', 'Microsoft'),
    ('meta', 'Meta'),
    ('netflix', 'Netflix'),
    ('startup-generic', 'Generic Startup');
