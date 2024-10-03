package com.challenge.productservice.application.getproductprice;

import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.domain.productprice.ProductPriceRepository;

import java.util.Optional;

import static com.challenge.productservice.application.getproductprice.GetProductPriceResponse.*;

public class GetProductPriceUseCase {

    private final ProductPriceRepository productPriceRepository;

    public GetProductPriceUseCase(ProductPriceRepository productPriceRepository) {
        this.productPriceRepository = productPriceRepository;
    }

    public GetProductPriceResponse execute(GetProductPriceRequest request) {
        Optional<ProductPrice> productPrice = productPriceRepository.findHighestPriorityPrice(
                request.productId(),
                request.brandId(),
                request.validAt()
        );

        return productPrice.isPresent() ? new Successful(productPrice.get()) : new ProductPriceNotFound();
    }
}
