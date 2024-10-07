package com.challenge.productservice.infrastructure.database.mapper;

import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;
import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.infrastructure.database.entity.ProductPriceEntity;

import javax.money.Monetary;

public class ProductPriceMapper {

    public ProductPrice toDomain(ProductPriceEntity entity) {
        return new ProductPrice(
                new BrandId(entity.getBrandId()),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPriceList(),
                new ProductId(entity.getProductId()),
                entity.getPriority(),
                entity.getPrice(),
                Monetary.getCurrency(entity.getCurrency())
        );
    }
}
