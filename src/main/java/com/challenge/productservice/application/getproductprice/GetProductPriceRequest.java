package com.challenge.productservice.application.getproductprice;

import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;

import java.time.LocalDateTime;

public record GetProductPriceRequest(
    ProductId productId,
    BrandId brandId,
    LocalDateTime validAt
) {}
