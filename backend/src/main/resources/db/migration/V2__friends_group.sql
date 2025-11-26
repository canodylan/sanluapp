-- ==============================
-- Grupos de amigos
-- ==============================
CREATE TABLE friend_group (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    user_in_charge BIGINT REFERENCES users(id),
    created_at     TIMESTAMP DEFAULT NOW()
);

CREATE TABLE group_members (
    group_id    BIGINT REFERENCES friend_group(id) ON DELETE CASCADE,
    user_id     BIGINT REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (group_id, user_id)
);
