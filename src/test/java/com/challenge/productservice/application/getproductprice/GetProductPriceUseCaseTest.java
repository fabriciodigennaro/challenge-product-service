package com.challenge.productservice.application.getproductprice;

import com.challenge.productservice.application.getproductprice.GetProductPriceResponse.ProductPriceNotFound;
import com.challenge.productservice.application.getproductprice.GetProductPriceResponse.Successful;
import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;
import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.domain.productprice.ProductPriceRepository;
import org.junit.jupiter.api.Test;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetProductPriceUseCaseTest {
    private final ProductPriceRepository productPriceRepository = mock(ProductPriceRepository.class);
    private final GetProductPriceUseCase useCase = new GetProductPriceUseCase(productPriceRepository);

    ProductId productId = new ProductId(2525);
    BrandId brandId = new BrandId(1);
    LocalDateTime validAt = LocalDateTime.now();
    LocalDateTime startDate = validAt.minusDays(1);
    LocalDateTime endDate = validAt.plusDays(1);

    ProductPrice expectedProductPrice = new ProductPrice(
            brandId,
            startDate,
            endDate,
            1,
            productId,
            1,
            BigDecimal.TEN,
            Monetary.getCurrency("EUR")
    );

    GetProductPriceRequest request = new GetProductPriceRequest(productId, brandId, validAt);

    @Test
    void shouldGetTheProductPrice() {
        // Given
        when(productPriceRepository.findHighestPriorityPrice(productId, brandId, validAt))
                .thenReturn(Optional.of(expectedProductPrice));

        // When
        GetProductPriceResponse response = useCase.execute(request);

        // Then
        assertThat(response).isEqualTo(new Successful(expectedProductPrice));
        verify(productPriceRepository).findHighestPriorityPrice(productId, brandId, validAt);
    }

    @Test
    void ShouldGetAPriceNotFoundResponseIfIsNotAMatch() {
        // Given
        when(productPriceRepository.findHighestPriorityPrice(productId, brandId, validAt)).thenReturn(Optional.empty());

        // When
        GetProductPriceResponse response = useCase.execute(request);

        // Then
        assertThat(response).isInstanceOf(ProductPriceNotFound.class);
    }
}