package com.challenge.productservice.component;

import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;
import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.infrastructure.entrypoint.rest.response.ProductPriceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@SpringBootTest
public class GetProductPriceComponentTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    ProductId productId = new ProductId(2525);
    BrandId brandId = new BrandId(1);
    Instant validAt = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Instant startDate = validAt.minus(1, ChronoUnit.DAYS);
    Instant endDate = validAt.plus(1, ChronoUnit.DAYS);
    BigDecimal price = new BigDecimal("9.99");
    int priceList = 1;
    String currency = "EUR";

    ProductPrice productPrice = new ProductPrice(
            brandId,
            startDate,
            endDate,
            priceList,
            productId,
            1,
            price,
            Monetary.getCurrency(currency)
    );

    ProductPriceResponse productPriceResponse = new ProductPriceResponse(
            productId.value(),
            brandId.value(),
            priceList,
            startDate,
            endDate,
            price,
            currency
    );

    @Test
    void getProductPrice() throws JsonProcessingException {
        // Given
        givenExistingProductPrice(productPrice);
        String expectedJson = objectMapper.writeValueAsString(productPriceResponse);

        // When
        MockMvcResponse response = whenARequestToGetAProductPriceIsReceived();

        // Then
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(CoreMatchers.equalTo(expectedJson));
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


    private MockMvcResponse whenARequestToGetAProductPriceIsReceived() {
        return RestAssuredMockMvc
                .given()
                .webAppContextSetup(context)
                .contentType(ContentType.JSON)
                .param("productId", productId.value())
                .param("brandId", brandId.value())
                .param("validAt", validAt.toString())
                .when()
                .get("/prices");
    }
}
