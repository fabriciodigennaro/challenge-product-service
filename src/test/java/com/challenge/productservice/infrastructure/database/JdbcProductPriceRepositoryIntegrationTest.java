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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

    LocalDateTime validAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    @Test
    void shouldGetProductPricesValidAtGivenDate() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());
        ProductPrice ProductPricePriorityZero = createProductPrice(
                brandId,
                productId,
                validAt.minusDays(1),
                validAt.plusDays(1),
                0
        );
        ProductPrice ProductPricePriorityOne = createProductPrice(
                brandId,
                productId,
                validAt.minusDays(1),
                validAt.plusDays(1),
                1
        );
        givenExistingProductPrice(ProductPricePriorityZero);
        givenExistingProductPrice(ProductPricePriorityOne);

        // When
        List<ProductPrice> result = productPriceRepository.getProductPrices(productId, brandId, validAt);

        // Then
        assertThat(result).isEqualTo(List.of(ProductPricePriorityZero, ProductPricePriorityOne));
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
        assertThat(result).isEqualTo(List.of(productPrice));
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
        assertThat(result).isEqualTo(List.of(productPrice));
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
        assertThat(result.size()).isEqualTo(0);
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
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void shouldNotGetAProductPriceWhenNonFound() {
        // Given
        ProductId productId = new ProductId(randomLong());
        BrandId brandId = new BrandId(randomLong());

        // When
        List<ProductPrice> result = productPriceRepository.getProductPrices(productId, brandId, validAt);

        // Then
        assertThat(result.size()).isEqualTo(0);
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
        assertThat(result.size()).isEqualTo(0);
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
        assertThat(result.size()).isEqualTo(0);
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