CREATE TABLE cat
(
    id BIGSERIAL PRIMARY KEY,
    cat_name VARCHAR(30),
    color VARCHAR(10),
    sold BOOLEAN DEFAULT FALSE,
    price FLOAT
);

CREATE TABLE cat_order
(
    id BIGSERIAL PRIMARY KEY,
    customer BIGINT REFERENCES account (id),
    payed BOOLEAN DEFAULT FALSE,
    cat_id BIGINT REFERENCES cat (id)
);

CREATE TABLE roles
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES account (id),
    user_role VARCHAR(10)
);
