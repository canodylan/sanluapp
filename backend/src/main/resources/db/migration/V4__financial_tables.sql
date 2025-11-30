-- =============================================
-- CUENTAS DEL CLUB
-- =============================================
CREATE TABLE club_accounts (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    current_balance NUMERIC(10,2) DEFAULT 0.00,
    is_primary      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- Marca una cuenta principal por defecto en instalaciones existentes
WITH first_account AS (
    SELECT id
    FROM club_accounts
    ORDER BY created_at ASC NULLS LAST, id ASC
    LIMIT 1
)
UPDATE club_accounts
SET is_primary = TRUE
WHERE id = (SELECT id FROM first_account)
  AND NOT EXISTS (SELECT 1 FROM club_accounts WHERE is_primary = TRUE);

CREATE UNIQUE INDEX IF NOT EXISTS uq_club_accounts_primary_true
    ON club_accounts (is_primary)
    WHERE is_primary;

-- =============================================
-- CATEGORÍAS FINANCIERAS
-- =============================================
CREATE TABLE money_categories (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) UNIQUE NOT NULL,
    color           VARCHAR(20) NOT NULL DEFAULT '#1976d2'
);

-- =============================================
-- TRANSACCIONES (INGRESOS / GASTOS / TRANSFERENCIAS)
-- =============================================
CREATE TABLE money_transactions (
    id                  BIGSERIAL PRIMARY KEY,
    type                VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE', 'TRANSFER')),
    amount              NUMERIC(10,2) NOT NULL CHECK (amount > 0),
    description         VARCHAR(255),
    category_id         BIGINT REFERENCES money_categories(id),
    account_from_id     BIGINT REFERENCES club_accounts(id),  -- Para expense o transfer
    account_to_id       BIGINT REFERENCES club_accounts(id),  -- Para income o transfer
    created_by          BIGINT REFERENCES users(id),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    transaction_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    -- Para enlazar transacciones con otras entidades del sistema
    related_entity_type VARCHAR(50),  -- Ej: 'membership_fee', 'money_expense', 'stock_purchase'
    related_entity_id   BIGINT
);

-- Índices útiles para las consultas más frecuentes
CREATE INDEX idx_money_transactions_type ON money_transactions(type);
CREATE INDEX idx_money_transactions_category ON money_transactions(category_id);
CREATE INDEX idx_money_transactions_created_at ON money_transactions(created_at);

-- =============================================
-- REGLAS DE CONSISTENCIA
-- =============================================

-- 1) Una transfer necesita cuenta origen y destino
ALTER TABLE money_transactions
ADD CONSTRAINT transfer_require_accounts
CHECK (
    (type <> 'TRANSFER')
    OR (account_from_id IS NOT NULL AND account_to_id IS NOT NULL)
);

-- 2) Income NO puede tener account_from_id
ALTER TABLE money_transactions
ADD CONSTRAINT income_no_source
CHECK (
    (type <> 'INCOME')
    OR (account_from_id IS NULL)
);

-- 3) Expense NO puede tener account_to_id
ALTER TABLE money_transactions
ADD CONSTRAINT expense_no_target
CHECK (
    (type <> 'EXPENSE')
    OR (account_to_id IS NULL)
);

-- =============================================
-- GASTOS DEL CLUB (SOLICITADOS Y APROBADOS)
-- =============================================
CREATE TABLE money_expenses (
    id              BIGSERIAL PRIMARY KEY,
    transaction_id  BIGINT REFERENCES money_transactions(id),
    description     VARCHAR(255) NOT NULL,
    amount          NUMERIC(10,2) NOT NULL CHECK (amount > 0),
    receipt_url     VARCHAR(500),
    requested_by    BIGINT NOT NULL REFERENCES users(id),
    approved        BOOLEAN DEFAULT FALSE,
    approved_by     BIGINT REFERENCES users(id),
    approved_at     TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    category_id     BIGINT REFERENCES money_categories (id),
    account_id      BIGINT REFERENCES club_accounts (id)
);

CREATE INDEX idx_money_expenses_category ON money_expenses (category_id);
CREATE INDEX idx_money_expenses_account ON money_expenses (account_id);
