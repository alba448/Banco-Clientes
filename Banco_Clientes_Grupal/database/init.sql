CREATE TABLE IF NOT EXISTS tarjetas (
    id SERIAL PRIMARY KEY,
    numeroTarjeta VARCHAR(16) NOT NULL,
    nombreTitular TEXT NOT NULL,
    fechaCaducidad DATE NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);