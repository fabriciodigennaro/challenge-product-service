package com.challenge.productservice.application.getproductprice;

import com.challenge.productservice.application.getproductprice.GetProductPriceResponse.ProductPriceNotFound;
import com.challenge.productservice.application.getproductprice.GetProductPriceResponse.Successful;
import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.domain.productprice.ProductPriceRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GetProductPriceUseCase {

    private final ProductPriceRepository productPriceRepository;

    public GetProductPriceUseCase(ProductPriceRepository productPriceRepository) {
        this.productPriceRepository = productPriceRepository;
    }

    public GetProductPriceResponse execute(GetProductPriceRequest request) {
        List<ProductPrice> productPrices = productPriceRepository.getProductPrices(
                request.productId(),
                request.brandId(),
                request.validAt()
        );

        Optional<ProductPrice> productPrice = productPrices.stream()
                .max(Comparator.comparingInt(ProductPrice::priority));

        return productPrice.isPresent() ? new Successful(productPrice.get()) : new ProductPriceNotFound();
    }
}
