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

--Insert example friend groups
INSERT INTO friend_group (name, user_in_charge) VALUES ('CB MENS', 1), ('Grupo Gema', 2);
INSERT INTO group_members (group_id, user_id) VALUES 
(1, 1), (1, 2), (1, 3),
(2, 4), (2, 5);