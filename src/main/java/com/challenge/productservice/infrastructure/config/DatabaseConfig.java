package com.challenge.productservice.infrastructure.config;

import com.challenge.productservice.domain.productprice.ProductPriceRepository;
import com.challenge.productservice.infrastructure.database.JdbcProductPriceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DatabaseConfig {

    @Bean
    public ProductPriceRepository productPriceRepository(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate
    ) {
        return new JdbcProductPriceRepository(namedParameterJdbcTemplate);
    }
}
