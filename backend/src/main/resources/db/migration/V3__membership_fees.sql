-- =============================================
-- Membership fees core tables
-- =============================================
CREATE TABLE membership_fees (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    year            INT NOT NULL,
    days_attending  INT NOT NULL DEFAULT 0,
    base_amount     NUMERIC(10,2) NOT NULL DEFAULT 0,
    discount_total  NUMERIC(10,2) NOT NULL DEFAULT 0,
    final_amount    NUMERIC(10,2) NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    paid_at         TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_membership_fee_user_year UNIQUE (user_id, year),
    CONSTRAINT chk_membership_fee_status CHECK (status IN ('PENDING', 'CALCULATED', 'PAID'))
);

CREATE TABLE membership_fee_days (
    id                  BIGSERIAL PRIMARY KEY,
    membership_fee_id   BIGINT NOT NULL REFERENCES membership_fees(id) ON DELETE CASCADE,
    attendance_date     DATE NOT NULL,
    CONSTRAINT uq_membership_fee_day UNIQUE (membership_fee_id, attendance_date)
);

CREATE TABLE membership_fee_discounts (
    id                  BIGSERIAL PRIMARY KEY,
    membership_fee_id   BIGINT NOT NULL REFERENCES membership_fees(id) ON DELETE CASCADE,
    concept             VARCHAR(255) NOT NULL,
    amount              NUMERIC(10,2) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Trigger to recalculate discount totals whenever discount rows change
CREATE OR REPLACE FUNCTION fn_recalc_discount_total() RETURNS TRIGGER AS $$
DECLARE
    target_id BIGINT := COALESCE(NEW.membership_fee_id, OLD.membership_fee_id);
    total_discount NUMERIC(10,2);
BEGIN
    SELECT COALESCE(SUM(amount), 0) INTO total_discount
    FROM membership_fee_discounts
    WHERE membership_fee_id = target_id;

    UPDATE membership_fees
    SET discount_total = total_discount,
        final_amount = base_amount - total_discount,
        updated_at = NOW()
    WHERE id = target_id;
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_membership_fee_discounts_ai
AFTER INSERT ON membership_fee_discounts
FOR EACH ROW EXECUTE FUNCTION fn_recalc_discount_total();

CREATE TRIGGER tr_membership_fee_discounts_au
AFTER UPDATE ON membership_fee_discounts
FOR EACH ROW EXECUTE FUNCTION fn_recalc_discount_total();

CREATE TRIGGER tr_membership_fee_discounts_ad
AFTER DELETE ON membership_fee_discounts
FOR EACH ROW EXECUTE FUNCTION fn_recalc_discount_total();
