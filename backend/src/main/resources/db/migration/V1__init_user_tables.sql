-- ==============================
-- Usuarios y roles
-- ==============================

CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    nickname        VARCHAR(50),
    name            VARCHAR(50),
    first_name      VARCHAR(50),
    last_name       VARCHAR(50),
    phone_number    VARCHAR(20),
    birthday        DATE DEFAULT NULL,
    --drink_type      VARCHAR(50), -- none, beer, wine, mixed, other
    join_at         TIMESTAMP DEFAULT NOW()
);

CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id     BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id     BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('USER');

-- Insert an example admin and user
INSERT INTO users (username, password_hash, email, nickname, name, first_name, last_name, phone_number, birthday, join_at) 
VALUES('admin', '1234', 'admin@admin.com', 'admin_nick', 'Admin Name', 'Garcia', 'Lopez', '692323966', '2000-01-01', NOW());

INSERT INTO users (username, password_hash, email, nickname, name, first_name, last_name, phone_number, birthday, join_at) 
VALUES('user', '1234', 'user@user.com', 'user_nick', 'User', 'Martinez', 'Gomez', '612345678', '1995-05-15', NOW());

-- Assign ADMIN role to the default admin user
WITH admin_user AS (
    SELECT id AS user_id FROM users WHERE username='admin'
),
admin_role AS (
    SELECT id AS role_id FROM roles WHERE name='ADMIN'
)
INSERT INTO user_roles (user_id, role_id)
SELECT admin_user.user_id, admin_role.role_id
FROM admin_user, admin_role;

-- Assign USER role to the default user
WITH normal_user AS (
    SELECT id AS user_id FROM users WHERE username='user'
),
user_role AS (
    SELECT id AS role_id FROM roles WHERE name='USER'
)
INSERT INTO user_roles (user_id, role_id)
SELECT normal_user.user_id, user_role.role_id
FROM normal_user, user_role;