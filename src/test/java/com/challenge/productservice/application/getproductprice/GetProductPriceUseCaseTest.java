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
import java.util.List;

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
    GetProductPriceRequest request = new GetProductPriceRequest(productId, brandId, validAt);

    @Test
    void shouldGetTheProductPriceWhenOnlyOnePriceIsFound() {
        // Given
        ProductPrice expectedProductPrice = createProductPriceWithPriority(0);
        when(productPriceRepository.getProductPrices(productId, brandId, validAt))
                .thenReturn(List.of(expectedProductPrice));

        // When
        GetProductPriceResponse response = useCase.execute(request);

        // Then
        assertThat(response).isEqualTo(new Successful(expectedProductPrice));
        verify(productPriceRepository).getProductPrices(productId, brandId, validAt);
    }

    @Test
    void shouldGetAProductPriceWithHighestPriorityWhenMultiplePricesFound() {
        // Given
        ProductPrice productPriceWithLessPriority = createProductPriceWithPriority(0);
        ProductPrice productPriceWithHighestPriority = createProductPriceWithPriority(1);
        when(productPriceRepository.getProductPrices(productId, brandId, validAt))
                .thenReturn(List.of(productPriceWithHighestPriority, productPriceWithLessPriority));

        // When
        GetProductPriceResponse response = useCase.execute(request);

        // Then
        assertThat(response).isEqualTo(new Successful(productPriceWithHighestPriority));
        verify(productPriceRepository).getProductPrices(productId, brandId, validAt);
    }

    @Test
    void ShouldGetAPriceNotFoundResponseIfNoneFound() {
        // Given
        when(productPriceRepository.getProductPrices(productId, brandId, validAt)).thenReturn(List.of());

        // When
        GetProductPriceResponse response = useCase.execute(request);

        // Then
        assertThat(response).isInstanceOf(ProductPriceNotFound.class);
    }

    private ProductPrice createProductPriceWithPriority(int priority) {
        return new ProductPrice(
                brandId,
                startDate,
                endDate,
                1,
                productId,
                priority,
                BigDecimal.TEN,
                Monetary.getCurrency("EUR")
        );
    }
}