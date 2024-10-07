package com.challenge.productservice.infrastructure.config;

import com.challenge.productservice.domain.productprice.ProductPriceRepository;
import com.challenge.productservice.infrastructure.database.JpaProductPriceRepository;
import com.challenge.productservice.infrastructure.database.mapper.ProductPriceMapper;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Bean
    public ProductPriceRepository productPriceRepository(
            EntityManager entityManager
    ) {
        return new JpaProductPriceRepository(entityManager, new ProductPriceMapper());
    }
}
