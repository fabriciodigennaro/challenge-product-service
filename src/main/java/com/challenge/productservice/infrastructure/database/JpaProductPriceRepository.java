package com.challenge.productservice.infrastructure.database;

import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;
import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.domain.productprice.ProductPriceRepository;
import com.challenge.productservice.infrastructure.database.entity.ProductPriceEntity;
import com.challenge.productservice.infrastructure.database.mapper.ProductPriceMapper;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class JpaProductPriceRepository implements ProductPriceRepository {
    private final EntityManager entityManager;
    private final ProductPriceMapper mapper;

    public JpaProductPriceRepository(EntityManager entityManager, ProductPriceMapper mapper) {
        this.entityManager = entityManager;
        this.mapper = mapper;
    }

    @Override
    public List<ProductPrice> getProductPrices(ProductId productId, BrandId brandId, LocalDateTime validAt) {
        String query =
                """
                    SELECT p FROM ProductPriceEntity p
                    WHERE p.productId = :productId
                    AND p.brandId = :brandId
                    AND p.startDate <= :validAt
                    AND p.endDate >= :validAt
                """;

        List<ProductPriceEntity> entities = entityManager.createQuery(query, ProductPriceEntity.class)
                .setParameter("productId", productId.value())
                .setParameter("brandId", brandId.value())
                .setParameter("validAt", validAt)
                .getResultList();

        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
