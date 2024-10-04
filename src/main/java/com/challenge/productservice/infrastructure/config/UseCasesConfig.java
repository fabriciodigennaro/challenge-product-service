package com.challenge.productservice.infrastructure.config;

import com.challenge.productservice.application.getproductprice.GetProductPriceUseCase;
import com.challenge.productservice.domain.productprice.ProductPriceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public GetProductPriceUseCase getProductPriceUseCase(
            ProductPriceRepository productPriceRepository
    ) {
        return new GetProductPriceUseCase(productPriceRepository);
    }
}
