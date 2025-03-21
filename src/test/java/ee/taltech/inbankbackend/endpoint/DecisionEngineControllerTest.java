package ee.taltech.inbankbackend.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.inbankbackend.dto.DecisionRequest;
import ee.taltech.inbankbackend.dto.DecisionResponse;
import ee.taltech.inbankbackend.exceptions.NoValidLoanException;
import ee.taltech.inbankbackend.service.DecisionEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class holds integration tests for the DecisionEngineController endpoint.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class DecisionEngineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DecisionEngine decisionEngine;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldReturnExpectedResponseForValidRequest() throws Exception {
        DecisionResponse decisionResponse = new DecisionResponse(5000L, 24, null);
        DecisionRequest request = new DecisionRequest("12345678901", 5000L, 24);

        when(decisionEngine.calculateApprovedLoan(any(DecisionRequest.class))).thenReturn(decisionResponse);

        performRequestAndAssert(request, status().isOk(), 5000L, 24, null);
    }

    @Test
    public void shouldReturnBadRequestForInvalidPersonalCode() throws Exception {
        DecisionRequest request = new DecisionRequest("invalid_code", 5000L, 24);

        when(decisionEngine.calculateApprovedLoan(any(DecisionRequest.class)))
                .thenThrow(new NoValidLoanException("Invalid personal code"));

        performRequestAndAssert(request, status().isBadRequest(), null, null, "Invalid personal code");
    }

    @Test
    public void shouldReturnBadRequestForOutOfBoundsLoanAmount() throws Exception {
        DecisionRequest request = new DecisionRequest("12345678901", 2000L, 24); // **Fixed value within valid range**

        performRequestAndAssert(request, status().isBadRequest(), null, null, "Loan amount out of bounds");
    }

    @Test
    public void shouldReturnBadRequestForOutOfBoundsLoanPeriod() throws Exception {
        DecisionRequest request = new DecisionRequest("12345678901", 5000L, 12); // **Fixed min valid value**

        performRequestAndAssert(request, status().isBadRequest(), null, null, "Loan period out of bounds");
    }

    @Test
    public void shouldReturnNotFoundWhenLoanRejectedByScoring() throws Exception {
        DecisionRequest request = new DecisionRequest("12345678901", 10000L, 60);

        when(decisionEngine.calculateApprovedLoan(any(DecisionRequest.class)))
                .thenThrow(new NoValidLoanException("No valid loan found!"));

        performRequestAndAssert(request, status().isNotFound(), null, null, "No valid loan found!");
    }

    @Test
    public void shouldReturnInternalServerErrorForUnexpectedError() throws Exception {
        DecisionRequest request = new DecisionRequest("12345678901", 5000L, 24);

        when(decisionEngine.calculateApprovedLoan(any(DecisionRequest.class)))
                .thenThrow(new RuntimeException());

        performRequestAndAssert(request, status().isInternalServerError(), null, null, "An unexpected error occurred");
    }

    // ✅ Utility method to reduce duplication in tests
    private void performRequestAndAssert(DecisionRequest request, ResultMatcher expectedStatus,
                                         Long expectedLoanAmount, Integer expectedLoanPeriod, String expectedErrorMessage) throws Exception {
        var resultActions = mockMvc.perform(post("/loan/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(expectedStatus)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // 🔹 Only assert non-null values to avoid mismatches
        if (expectedLoanAmount != null) {
            resultActions.andExpect(jsonPath("$.loanAmount").value(expectedLoanAmount));
        } else {
            resultActions.andExpect(jsonPath("$.loanAmount").doesNotExist());
        }

        if (expectedLoanPeriod != null) {
            resultActions.andExpect(jsonPath("$.loanPeriod").value(expectedLoanPeriod));
        } else {
            resultActions.andExpect(jsonPath("$.loanPeriod").doesNotExist());
        }

        if (expectedErrorMessage != null) {
            resultActions.andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage));
        } else {
            resultActions.andExpect(jsonPath("$.errorMessage").doesNotExist());
        }
    }
}




