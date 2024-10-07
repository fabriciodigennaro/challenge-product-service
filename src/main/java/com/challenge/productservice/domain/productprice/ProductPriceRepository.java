package com.challenge.productservice.domain.productprice;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductPriceRepository {
    List<ProductPrice> getProductPrices(ProductId productId, BrandId brandId, LocalDateTime validAt);
}
