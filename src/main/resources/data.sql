CREATE TABLE prices (
    brand_id    BIGINT          NOT NULL,
    start_date  TIMESTAMP       NOT NULL,
    end_date    TIMESTAMP       NOT NULL,
    price_list  INT             NOT NULL,
    product_id  BIGINT          NOT NULL,
    priority    INT             NOT NULL,
    price       DECIMAL(10, 2)  NOT NULL,
    currency    VARCHAR(3)      NOT NULL
);

INSERT INTO prices (brand_id, start_date, end_date, price_list, product_id, priority, price, currency) VALUES
(1, '2020-06-14 00:00:00', '2020-12-31 23:59:59', 1, 35455, 0, 35.5, 'EUR'),
(1, '2020-06-14 15:00:00', '2020-06-14 18:30:00', 2, 35455, 1, 25.45, 'EUR'),
(1, '2020-06-15 00:00:00', '2020-06-15 11:00:00', 3, 35455, 1, 30.5, 'EUR'),
(1, '2020-06-15 16:00:00', '2020-12-31 23:59:59', 4, 35455, 1, 38.95, 'EUR');