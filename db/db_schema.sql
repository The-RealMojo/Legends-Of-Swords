-- ================================================================
-- Legends of Sword and Wand — MySQL Database Schema
-- Run this script once to set up the database.
-- ================================================================

CREATE DATABASE IF NOT EXISTS legends_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE legends_db;

-- Users
CREATE TABLE IF NOT EXISTS Users (
    user_id  INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(64)  NOT NULL,        -- SHA-256 hex digest
    wins     INT          NOT NULL DEFAULT 0,
    losses   INT          NOT NULL DEFAULT 0
);

-- Saved Parties  (up to 5 per user)
CREATE TABLE IF NOT EXISTS Parties (
    user_id      INT          NOT NULL,
    party_name   VARCHAR(100) NOT NULL,
    current_room INT          NOT NULL DEFAULT 0,
    gold         INT          NOT NULL DEFAULT 500,
    PRIMARY KEY (user_id, party_name),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- Heroes stored within a saved party
CREATE TABLE IF NOT EXISTS PartyHeroes (
    user_id             INT          NOT NULL,
    party_name          VARCHAR(100) NOT NULL,
    hero_index          INT          NOT NULL,
    hero_name           VARCHAR(50)  NOT NULL,
    level               INT          NOT NULL DEFAULT 1,
    attack              INT          NOT NULL DEFAULT 5,
    defense             INT          NOT NULL DEFAULT 5,
    hp                  INT          NOT NULL DEFAULT 100,
    max_hp              INT          NOT NULL DEFAULT 100,
    mana                INT          NOT NULL DEFAULT 50,
    max_mana            INT          NOT NULL DEFAULT 50,
    active_class        VARCHAR(20)  NOT NULL DEFAULT 'WARRIOR',
    hybrid_class        VARCHAR(20)  NOT NULL DEFAULT 'NONE',
    hybridized          TINYINT(1)   NOT NULL DEFAULT 0,
    order_level         INT          NOT NULL DEFAULT 0,
    chaos_level         INT          NOT NULL DEFAULT 0,
    warrior_level       INT          NOT NULL DEFAULT 0,
    mage_level          INT          NOT NULL DEFAULT 0,
    current_exp         INT          NOT NULL DEFAULT 0,
    item_purchase_score INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id, party_name, hero_index),
    FOREIGN KEY (user_id, party_name)
        REFERENCES Parties(user_id, party_name) ON DELETE CASCADE
);

-- Inventory stored within a saved party
CREATE TABLE IF NOT EXISTS PartyInventory (
    user_id      INT          NOT NULL,
    party_name   VARCHAR(100) NOT NULL,
    item_name    VARCHAR(50)  NOT NULL,
    quantity     INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id, party_name, item_name),
    FOREIGN KEY (user_id, party_name)
        REFERENCES Parties(user_id, party_name) ON DELETE CASCADE
);

-- Campaign end-of-run scores
CREATE TABLE IF NOT EXISTS Scores (
    score_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL,
    score      INT NOT NULL,
    scored_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- PvP match history
CREATE TABLE IF NOT EXISTS PvpMatches (
    match_id   INT AUTO_INCREMENT PRIMARY KEY,
    winner_id  INT NOT NULL,
    loser_id   INT NOT NULL,
    played_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (winner_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (loser_id)  REFERENCES Users(user_id) ON DELETE CASCADE
);

