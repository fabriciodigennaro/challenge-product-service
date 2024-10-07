package com.challenge.productservice.infrastructure.database;

import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;
import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.domain.productprice.ProductPriceRepository;
import com.challenge.productservice.infrastructure.config.DatabaseConfig;
import com.challenge.productservice.infrastructure.database.entity.ProductPriceEntity;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.money.Monetary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({
        DatabaseConfig.class
})
class JpaProductPriceRepositoryIntegrationTest {

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    LocalDateTime validAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    @Test
    void shouldGetProductPricesValidAtGivenDate() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPricePriorityZero = createProductPrice(
                brandId,
                productId,
                validAt.minusDays(1),
                validAt.plusDays(1),
                0
        );
        ProductPrice productPricePriorityOne = createProductPrice(
                brandId,
                productId,
                validAt.minusDays(1),
                validAt.plusDays(1),
                1
        );
        givenExistingProductPrice(productPricePriorityZero);
        givenExistingProductPrice(productPricePriorityOne);

        // When
        List<ProductPrice> result = productPriceRepository.getProductPrices(productId, brandId, validAt);

        // Then
        assertThat(result).isEqualTo(List.of(productPricePriorityZero, productPricePriorityOne));
    }

    @Test
    void shouldGetProductPriceWhenStartDateIsEqualToValidAtDate() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt,
                validAt.plusDays(1),
                0
        );
        givenExistingProductPrice(productPrice);

        // When
        List<ProductPrice> result = productPriceRepository.getProductPrices(productId, brandId, validAt);

        // Then
        AssertionsForClassTypes.assertThat(result).isEqualTo(List.of(productPrice));
    }

    @Test
    void shouldGetProductPriceWhenEndDateIsEqualToValidAtDate() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.minusDays(1),
                validAt,
                0
        );
        givenExistingProductPrice(productPrice);

        // When
        List<ProductPrice> result = productPriceRepository.getProductPrices(productId, brandId, validAt);

        // Then
        AssertionsForClassTypes.assertThat(result).isEqualTo(List.of(productPrice));
    }

    @Test
    void shouldNotGetAProductPriceWhenEndedBeforeValidAtDate() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.minusDays(2),
                validAt.minusDays(1),
                0
        );
        givenExistingProductPrice(productPrice);

        // When
        List<ProductPrice> result = productPriceRepository.getProductPrices(productId, brandId, validAt);

        // Then
        AssertionsForClassTypes.assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void shouldNotGetAProductPriceWhenStartsAfterValidAtDate() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.plusDays(1),
                validAt.plusDays(2),
                0
        );
        givenExistingProductPrice(productPrice);

        // When
        List<ProductPrice> result = productPriceRepository.getProductPrices(productId, brandId, validAt);

        // Then
        AssertionsForClassTypes.assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void shouldNotGetAProductPriceWhenNonFound() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());

        // When
        List<ProductPrice> result = productPriceRepository.getProductPrices(productId, brandId, validAt);

        // Then
        AssertionsForClassTypes.assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void shouldNotGetAProductPriceWithDifferentBrandIdAndSameProductId() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.minusDays(1),
                validAt.plusDays(1),
                0
        );
        givenExistingProductPrice(productPrice);

        BrandId anotherBrandId = new BrandId(randomLong());

        // When
        List<ProductPrice> result = productPriceRepository.getProductPrices(productId, anotherBrandId, validAt);

        // Then
        AssertionsForClassTypes.assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void shouldNotGetAProductPriceWithDifferentProductIdAndSameBrandId() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.minusDays(1),
                validAt.plusDays(1),
                0
        );
        givenExistingProductPrice(productPrice);

        ProductId anotherProductId = new ProductId(randomLong());

        // When
        List<ProductPrice> result = productPriceRepository.getProductPrices(anotherProductId, brandId, validAt);

        // Then
        AssertionsForClassTypes.assertThat(result.size()).isEqualTo(0);
    }

    private void givenExistingProductPrice(ProductPrice productPrice) {
        ProductPriceEntity entity = new ProductPriceEntity();
        entity.setId(UUID.randomUUID());
        entity.setBrandId(productPrice.brandId().value());
        entity.setStartDate(productPrice.startDate());
        entity.setEndDate(productPrice.endDate());
        entity.setPriceList(productPrice.priceList());
        entity.setProductId(productPrice.productId().value());
        entity.setPriority(productPrice.priority());
        entity.setPrice(productPrice.price());
        entity.setCurrency(productPrice.currency().getCurrencyCode());

        entityManager.persist(entity);
    }

    private long randomLong() {
        return Math.round(Math.random() * 1000);
    }

    private ProductPrice createProductPrice(
            BrandId brandId,
            ProductId productId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int priority
    ) {
        return new ProductPrice(
                brandId,
                startDate,
                endDate,
                1,
                productId,
                priority,
                new BigDecimal("9.99"),
                Monetary.getCurrency("EUR")
        );
    }
}
