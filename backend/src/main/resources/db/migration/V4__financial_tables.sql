-- =============================================
-- CUENTAS DEL CLUB
-- =============================================
CREATE TABLE club_accounts (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    current_balance NUMERIC(10,2) DEFAULT 0.00, 
    created_at      TIMESTAMP DEFAULT NOW()
);

-- =============================================
-- CATEGORÍAS FINANCIERAS
-- =============================================
CREATE TABLE money_categories (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) UNIQUE NOT NULL,
    description     TEXT
);

-- =============================================
-- TRANSACCIONES (INGRESOS / GASTOS / TRANSFERENCIAS)
-- =============================================
CREATE TABLE money_transactions (
    id                  BIGSERIAL PRIMARY KEY,
    type                VARCHAR(20) NOT NULL CHECK (type IN ('income', 'expense', 'transfer')),
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

-- Index útil
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
    (type <> 'transfer')
    OR (account_from_id IS NOT NULL AND account_to_id IS NOT NULL)
);

-- 2) Income NO puede tener account_from_id
ALTER TABLE money_transactions
ADD CONSTRAINT income_no_source
CHECK (
    (type <> 'income')
    OR (account_from_id IS NULL)
);

-- 3) Expense NO puede tener account_to_id
ALTER TABLE money_transactions
ADD CONSTRAINT expense_no_target
CHECK (
    (type <> 'expense')
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
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
