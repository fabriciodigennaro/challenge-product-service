package com.challenge.productservice.infrastructure.database;

import com.challenge.productservice.domain.productprice.BrandId;
import com.challenge.productservice.domain.productprice.ProductId;
import com.challenge.productservice.domain.productprice.ProductPrice;
import com.challenge.productservice.domain.productprice.ProductPriceRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.money.Monetary;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

public class JdbcProductPriceRepository implements ProductPriceRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcProductPriceRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Optional<ProductPrice> findHighestPriorityPrice(ProductId productId, BrandId brandId, Instant validAt) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productId", productId.value())
                .addValue("brandId", brandId.value())
                .addValue("validAt", validAt);

        return namedParameterJdbcTemplate.query(
                """
                    SELECT * FROM prices
                    WHERE product_id = :productId
                    AND brand_id = :brandId
                    AND start_date <= :validAt
                    AND end_date >= :validAt
                    ORDER BY priority DESC
                """,
                params,
                new ProductPriceRowMapper()
        ).stream().findFirst();
    }

    private static class ProductPriceRowMapper implements RowMapper<ProductPrice> {

        @Override
        public ProductPrice mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ProductPrice(
                    new BrandId(rs.getInt("brand_id")),
                    rs.getTimestamp("start_date").toInstant(),
                    rs.getTimestamp("end_date").toInstant(),
                    rs.getInt("price_list"),
                    new ProductId(rs.getInt("product_id")),
                    rs.getInt("priority"),
                    rs.getBigDecimal("price"),
                    Monetary.getCurrency(rs.getString("currency"))
            );
        }
    }
}
