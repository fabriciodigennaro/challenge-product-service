package com.challenge.productservice.domain.productprice;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProductPriceRepository {
    Optional<ProductPrice> findHighestPriorityPrice(ProductId productId, BrandId brandId, LocalDateTime validAt);
}
