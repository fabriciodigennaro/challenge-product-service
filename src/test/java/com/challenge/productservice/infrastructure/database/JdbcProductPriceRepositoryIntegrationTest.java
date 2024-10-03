package com.challenge.productservice.infrastructure.database;

import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;
import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.domain.productprice.ProductPriceRepository;
import com.challenge.productservice.infrastructure.config.DatabaseConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        DatabaseConfig.class
})
class JdbcProductPriceRepositoryIntegrationTest {

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    Instant validAt = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Test
    void shouldGetProductPriceValidAtGivenDate() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.minus(1, ChronoUnit.DAYS),
                validAt.plus(1, ChronoUnit.DAYS),
                0
        );
        givenExistingProductPrice(productPrice);

        // When
        Optional<ProductPrice> result = productPriceRepository.findHighestPriorityPrice(productId, brandId, validAt);

        // Then
        assertThat(result).isEqualTo(Optional.of(productPrice));
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
                validAt.plus(1, ChronoUnit.DAYS),
                0
        );
        givenExistingProductPrice(productPrice);

        // When
        Optional<ProductPrice> result = productPriceRepository.findHighestPriorityPrice(productId, brandId, validAt);

        // Then
        assertThat(result).isEqualTo(Optional.of(productPrice));
    }

    @Test
    void shouldGetProductPriceWhenEndDateIsEqualToValidAtDate() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.minus(1, ChronoUnit.DAYS),
                validAt,
                0
        );
        givenExistingProductPrice(productPrice);

        // When
        Optional<ProductPrice> result = productPriceRepository.findHighestPriorityPrice(productId, brandId, validAt);

        // Then
        assertThat(result).isEqualTo(Optional.of(productPrice));
    }

    @Test
    void shouldGetProductPriceWithHighestPriority() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice overridenProductPrice = createProductPrice(
                brandId,
                productId,
                validAt.minus(1, ChronoUnit.DAYS),
                validAt.plus(1, ChronoUnit.DAYS),
                0
        );
        givenExistingProductPrice(overridenProductPrice);
        ProductPrice expectedProductPrice = createProductPrice(
                brandId,
                productId,
                validAt.minus(1, ChronoUnit.DAYS),
                validAt.plus(1, ChronoUnit.DAYS),
                1
        );
        givenExistingProductPrice(expectedProductPrice);

        // When
        Optional<ProductPrice> result = productPriceRepository.findHighestPriorityPrice(productId, brandId, validAt);

        // Then
        assertThat(result).isEqualTo(Optional.of(expectedProductPrice));
    }

    @Test
    void shouldNotGetAProductPriceWhenEndedBeforeValidAtDate() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.minus(2, ChronoUnit.DAYS),
                validAt.minus(1, ChronoUnit.DAYS),
                0
        );
        givenExistingProductPrice(productPrice);

        // When
        Optional<ProductPrice> result = productPriceRepository.findHighestPriorityPrice(productId, brandId, validAt);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotGetAProductPriceWhenStartsAfterValidAtDate() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.plus(1, ChronoUnit.DAYS),
                validAt.plus(2, ChronoUnit.DAYS),
                0
        );
        givenExistingProductPrice(productPrice);

        // When
        Optional<ProductPrice> result = productPriceRepository.findHighestPriorityPrice(productId, brandId, validAt);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotGetAProductPriceWhenNonFound() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());

        // When
        Optional<ProductPrice> result = productPriceRepository.findHighestPriorityPrice(productId, brandId, validAt);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotGetAProductPriceWithDifferentBrandIdAndSameProductId() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.minus(1, ChronoUnit.DAYS),
                validAt.plus(1, ChronoUnit.DAYS),
                0
        );
        givenExistingProductPrice(productPrice);

        BrandId anotherBrandId = new BrandId(randomLong());

        // When
        Optional<ProductPrice> result = productPriceRepository.findHighestPriorityPrice(productId, anotherBrandId, validAt);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotGetAProductPriceWithDifferentProductIdAndSameBrandId() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice productPrice = createProductPrice(
                brandId,
                productId,
                validAt.minus(1, ChronoUnit.DAYS),
                validAt.plus(1, ChronoUnit.DAYS),
                0
        );
        givenExistingProductPrice(productPrice);

        ProductId anotherProductId = new ProductId(randomLong());

        // When
        Optional<ProductPrice> result = productPriceRepository.findHighestPriorityPrice(anotherProductId, brandId, validAt);

        // Then
        assertThat(result).isEmpty();
    }

    private void givenExistingProductPrice(ProductPrice productPrice) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("brandId", productPrice.brandId().value())
                .addValue("startDate", productPrice.startDate())
                .addValue("endDate", productPrice.endDate())
                .addValue("priceList", productPrice.priceList())
                .addValue("productId", productPrice.productId().value())
                .addValue("priority", productPrice.priority())
                .addValue("price", productPrice.price())
                .addValue("currency", productPrice.currency().getCurrencyCode());

        namedParameterJdbcTemplate.update(
                """
                    INSERT INTO prices(brand_id, start_date, end_date, price_list, product_id, priority, price, currency)
                    VALUES (:brandId, :startDate, :endDate, :priceList, :productId, :priority, :price, :currency)
                """,
                params
        );
    }

    private long randomLong() {
        return Math.round(Math.random() * 1000);
    }

    private ProductPrice createProductPrice(
            BrandId brandId,
            ProductId productId,
            Instant startDate,
            Instant endDate,
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