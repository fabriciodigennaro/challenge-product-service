package com.challenge.productservice.domain.productprice;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductPrice(
    BrandId brandId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    int priceList,
    ProductId productId,
    int priority,
    BigDecimal price,
    CurrencyUnit currency
) {}
