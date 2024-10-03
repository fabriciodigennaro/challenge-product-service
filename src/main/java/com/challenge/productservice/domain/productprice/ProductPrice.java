package com.challenge.productservice.domain.productprice;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.time.Instant;

public record ProductPrice(
    BrandId brandId,
    Instant startDate,
    Instant endDate,
    int priceList,
    ProductId productId,
    int priority,
    BigDecimal price,
    CurrencyUnit currency
) {}
