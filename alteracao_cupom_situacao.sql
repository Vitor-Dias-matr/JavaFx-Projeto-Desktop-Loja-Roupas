USE lojas_roupas;

ALTER TABLE cupom
    ADD COLUMN situacao BOOLEAN NOT NULL DEFAULT TRUE AFTER data_validade;
