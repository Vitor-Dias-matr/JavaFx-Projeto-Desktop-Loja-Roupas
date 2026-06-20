CREATE DATABASE IF NOT EXISTS lojas_roupas;
USE lojas_roupas;

-- 1. Tabela de Produtos
CREATE TABLE IF NOT EXISTS produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    categoria int NOT NULL,
    tamanho VARCHAR(10) NOT NULL,
    cor VARCHAR(50) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    data_cadastro TIMESTAMP null,
    data_alteracao TIMESTAMP NULL 
);


ALTER TABLE produto 
ADD COLUMN quantidade INT NOT NULL DEFAULT 0;

-- 2. Tabela de Cupons de Desconto
CREATE TABLE IF NOT EXISTS cupom (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    porcentagem_desconto DECIMAL(5,2) NOT NULL, -- Ex: 10.00 para 10%
    quantidade INT NOT NULL DEFAULT 0,
    data_validade TIMESTAMP NULL,
    situacao BOOLEAN NOT NULL DEFAULT TRUE
);

-- 3. Tabela de Vendas (Cabeçalho da Venda)
CREATE TABLE IF NOT EXISTS venda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subtotal DECIMAL(10,2) NOT NULL,
    desconto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL, -- Dinheiro, Cartão, Pix
    cupom_id INT NULL,
    data_venda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cupom_id) REFERENCES cupom(id)
);

-- 4. Tabela de Itens da Venda (Produtos inclusos em cada venda)
CREATE TABLE IF NOT EXISTS item_venda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venda_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (venda_id) REFERENCES venda(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

select * from produto;
select * from cupom;
select * from venda;
select * from item_venda;
