package com.challenge.productservice.component;

import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;
import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.infrastructure.database.entity.ProductPriceEntity;
import com.challenge.productservice.infrastructure.entrypoint.rest.response.ProductPriceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import jakarta.persistence.EntityManager;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@SpringBootTest
@Transactional
public class GetProductPriceComponentTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    ProductId productId = new ProductId(2525);
    BrandId brandId = new BrandId(1);
    LocalDateTime validAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    LocalDateTime startDate = validAt.minusDays(1);
    LocalDateTime endDate = validAt.plusDays(1);
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
