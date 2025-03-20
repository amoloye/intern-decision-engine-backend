package ee.taltech.inbankbackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Holds the request data of the REST endpoint
 */
public record DecisionRequest(
        @NotBlank(message = "Personal code is required")
        String personalCode,

        @NotNull(message = "Loan amount is required")
        @Min(value = 2000, message = "Loan amount must be at least 2000")
        @Max(value = 10000, message = "Loan amount must not exceed 10000")
        Long loanAmount,

        @Min(value = 12, message = "Loan period must be at least 12 months")
        @Max(value = 60, message = "Loan period must not exceed 60 months")
        int loanPeriod
) {}
