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
    join_at         DATE DEFAULT NULL
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

-- Insert roles
INSERT INTO public.roles (name) VALUES
	 ('ADMIN'),
	 ('PRESIDENT'),
	 ('DIRECTIVE'),
	 ('RESPONSIBLE'),
	 ('USER');

