package com.challenge.productservice.infrastructure.entrypoint.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record Problem(
        @Schema(
                description = "Error detail",
                example = "Problem found"
        )
        String detail
) {}
