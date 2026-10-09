-- Creazione del database (se non esiste)
CREATE DATABASE IF NOT EXISTS chesstore_db;
USE chesstore_db;

-- 1. Tabella Categoria
CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descrizione VARCHAR(255)
);

-- 2. Tabella Utente
CREATE TABLE utente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    ruolo VARCHAR(20) NOT NULL DEFAULT 'CLIENTE'
);

-- 3. Tabella Prodotto (Catalogo con Foreign Key verso Categoria)
CREATE TABLE prodotto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descrizione TEXT,
    prezzo DECIMAL(10,2) NOT NULL,
    quantita_disponibile INT NOT NULL,
    immagine VARCHAR(255),
    id_categoria INT NOT NULL,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id)
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
);

-- 4. Tabella Ordine
CREATE TABLE ordine (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    totale DECIMAL(10,2) NOT NULL,
    indirizzo_spedizione VARCHAR(255) NOT NULL,
    metodo_pagamento VARCHAR(50) NOT NULL,
    utente_email VARCHAR(100) NOT NULL,
    FOREIGN KEY (utente_email) REFERENCES utente(email)
        ON UPDATE CASCADE 
        ON DELETE CASCADE
);

-- 5. Tabella Composizione Ordine (Storico Prezzi e Eliminazione Sicura)
CREATE TABLE composizione_ordine (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ordine INT NOT NULL,
    id_prodotto INT NULL,
    quantita INT NOT NULL,
    prezzo_unitario_storico DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_ordine) REFERENCES ordine(id) 
        ON DELETE CASCADE,
    FOREIGN KEY (id_prodotto) REFERENCES prodotto(id) 
        ON DELETE SET NULL
);

-- ==========================================================
-- DATI INIZIALI DI TEST (SEED DATA)
-- ==========================================================

-- Popolamento Categorie
INSERT INTO categoria (nome, descrizione) VALUES
('Scacchiere', 'Scacchiere in legno pregiato, regolamentari e da viaggio'),
('Set Pezzi', 'Pezzi piombati stile Staunton in vari legni e formati'),
('Orologi', 'Orologi digitali DGT omologati FIDE e analogici da torneo'),
('Accessori e Libri', 'Borse porta scacchi, taccuini per formulari e manuali didattici');

-- Popolamento Utenti (Admin e Cliente di test)
INSERT INTO utente (nome, cognome, email, password, ruolo) VALUES
('Mario', 'Rossi', 'admin@chesstore.it', 'admin123', 'ADMIN'),
('Luigi', 'Verdi', 'luigi.verdi@gmail.com', 'cliente123', 'CLIENTE');

-- Popolamento Prodotti
INSERT INTO prodotto (nome, descrizione, prezzo, quantita_disponibile, immagine, id_categoria) VALUES
('Scacchiera Staunton No. 5 Noce', 'Scacchiera intarsiata in legno di noce e acero, caselle 50mm.', 75.00, 15, 'scacchiera_noce_5.jpg', 1),
('Set Pezzi Staunton Timeless No. 5', 'Pezzi in bosso ed ebonite, doppio piombati, altezza re 89mm.', 65.50, 20, 'pezzi_staunton_5.jpg', 2),
('DGT 2010 Digital Chess Clock', 'Orologio digitale ufficiale FIDE con tempi Fischer e Bronstein.', 54.90, 10, 'dgt_2010.jpg', 3),
('Borsa da Torneo Standard', 'Sacca imbottita per scacchiera arrotolabile, pezzi e orologio.', 18.00, 30, 'borsa_torneo.jpg', 4);

