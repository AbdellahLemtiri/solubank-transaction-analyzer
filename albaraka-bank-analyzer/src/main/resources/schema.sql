-- DDL for Client, Compte, Transaction tables
CREATE TABLE Client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE Compte (
    id SERIAL PRIMARY KEY,
    type_compte VARCHAR(50),
    solde DECIMAL(15,2),
    client_id INT REFERENCES Client(id)
);

CREATE TABLE Transaction (
    id SERIAL PRIMARY KEY,
    date TIMESTAMP,
    montant DECIMAL(15,2),
    type VARCHAR(50),
    lieu VARCHAR(100),
    compte_id INT REFERENCES Compte(id)
);
