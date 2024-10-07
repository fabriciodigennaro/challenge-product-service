package com.challenge.productservice.infrastructure.entrypoint.rest;

import com.challenge.productservice.application.getproductprice.GetProductPriceRequest;
import com.challenge.productservice.application.getproductprice.GetProductPriceResponse;
import com.challenge.productservice.application.getproductprice.GetProductPriceResponse.ProductPriceNotFound;
import com.challenge.productservice.application.getproductprice.GetProductPriceResponse.Successful;
import com.challenge.productservice.application.getproductprice.GetProductPriceUseCase;
import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;
import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.infrastructure.config.ObjectMapperConfig;
import com.challenge.productservice.infrastructure.entrypoint.rest.response.ProductPriceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@Import({ObjectMapperConfig.class})
@WebMvcTest(controllers = ProductPriceController.class)
class ProductPriceControllerContractTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetProductPriceUseCase getProductPriceUseCase;

    ProductId productId = new ProductId(2525);
    BrandId brandId = new BrandId(1);
    LocalDateTime validAt = LocalDateTime.now();
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
    GetProductPriceRequest useCaseRequest = new GetProductPriceRequest(productId, brandId, validAt);

    @Test
    void getProductPrice() throws JsonProcessingException {
        // Given
        GetProductPriceResponse useCaseResponse = new Successful(productPrice);
        when(getProductPriceUseCase.execute(useCaseRequest)).thenReturn(useCaseResponse);

        ProductPriceResponse productPriceResponse = new ProductPriceResponse(
                productId.value(),
                brandId.value(),
                priceList,
                startDate,
                endDate,
                price,
                currency
        );
        String expectedJsonResponse = objectMapper.writeValueAsString(productPriceResponse);

        // When
        MockMvcResponse response = whenARequestToGetAProductPriceIsReceived();

        // Then
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(CoreMatchers.equalTo(expectedJsonResponse));

        verify(getProductPriceUseCase).execute(useCaseRequest);
    }

    @Test
    void shouldReturn404WhenPriceNotFoundForGivenParameters() {
        // Given
        GetProductPriceResponse useCaseResponse = new ProductPriceNotFound();
        when(getProductPriceUseCase.execute(useCaseRequest)).thenReturn(useCaseResponse);

        // When
        MockMvcResponse response = whenARequestToGetAProductPriceIsReceived();

        // Then
        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("detail", CoreMatchers.equalTo("Price not found for given parameters."));

        verify(getProductPriceUseCase).execute(useCaseRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {"productId", "brandId", "validAt"})
    void shouldReturn400WhenParamIsMissing(String missingParamName) {
        // When
        MockMvcResponse response = whenARequestToGetAProductPriceWithMissingParamIsReceived(missingParamName);

        // Then
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("detail", CoreMatchers.equalTo(
                        String.format(
                                "Required request parameter '%s' is not present", missingParamName)
                        )
                );

        verifyNoInteractions(getProductPriceUseCase);
    }

    @Test
    void shouldReturn400WhenValidAtParamIsNull() {
        // When
        MockMvcResponse response = whenARequestToGetAProductPriceIsReceived(String.valueOf(productId.value()), String.valueOf(brandId.value()), null);

        // Then
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("detail", CoreMatchers.equalTo("Parameter 'validAt' has an invalid type"));

        verifyNoInteractions(getProductPriceUseCase);
    }

    @Test
    void shouldReturn400WhenValidAtParamHasInvalidDateTimeFormat() {
        // When
        String validAt = "2024/7/2";
        MockMvcResponse response = whenARequestToGetAProductPriceIsReceived(String.valueOf(productId.value()), String.valueOf(brandId.value()), validAt);

        // Then
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("detail", CoreMatchers.equalTo("Parameter 'validAt' has an invalid type"));

        verifyNoInteractions(getProductPriceUseCase);
    }

    @Test
    void shouldReturn400WhenProductIdParamHasInvalidType() {
        // When
        MockMvcResponse response = whenARequestToGetAProductPriceIsReceived(String.valueOf(productId.value()), "invalid long", validAt.toString());

        // Then
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("detail", CoreMatchers.equalTo("Parameter 'brandId' has an invalid type"));

        verifyNoInteractions(getProductPriceUseCase);
    }

    @Test
    void shouldReturn400WhenBrandIdParamHasInvalidType() {
        // When
        MockMvcResponse response = whenARequestToGetAProductPriceIsReceived(String.valueOf(productId.value()), "invalid long", validAt.toString());

        // Then
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("detail", CoreMatchers.equalTo("Parameter 'brandId' has an invalid type"));

        verifyNoInteractions(getProductPriceUseCase);
    }

    @Test
    void shouldReturn500WhenErrorOccurs() {
        // Given
        when(getProductPriceUseCase.execute(useCaseRequest)).thenThrow(new RuntimeException("Unexpected error"));

        // When
        MockMvcResponse response = whenARequestToGetAProductPriceIsReceived();

        // Then
        response.then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("detail", CoreMatchers.equalTo("An unexpected error occurred"));

        verify(getProductPriceUseCase).execute(useCaseRequest);
    }

    private MockMvcResponse whenARequestToGetAProductPriceIsReceived() {
        return whenARequestToGetAProductPriceIsReceived(
            String.valueOf(productId.value()),
            String.valueOf(brandId.value()),
            validAt.toString()
        );
    }

    private MockMvcResponse whenARequestToGetAProductPriceIsReceived(
            String productId,
        String brandId,
        String validAt
    ) {
        return RestAssuredMockMvc
                .given()
                .webAppContextSetup(context)
                .contentType(ContentType.JSON)
                .param("productId", productId)
                .param("brandId", brandId)
                .param("validAt", validAt)
                .when()
                .get("/prices");
    }

    private MockMvcResponse whenARequestToGetAProductPriceWithMissingParamIsReceived(String missingParam) {
        var mock = RestAssuredMockMvc
                .given()
                .webAppContextSetup(context)
                .contentType(ContentType.JSON);

        if (!"productId".equals(missingParam)) {
           mock.param("productId", productId.value());
        }

        if (!"brandId".equals(missingParam)) {
            mock.param("brandId", productId.value());
        }

        if (!"validAt".equals(missingParam)) {
            mock.param("validAt", productId.value());
        }

        return mock
                .when()
                .get("/prices");
    }

}