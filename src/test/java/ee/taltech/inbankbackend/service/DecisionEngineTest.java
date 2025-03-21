package ee.taltech.inbankbackend.service;

import ee.taltech.inbankbackend.dto.DecisionRequest;
import ee.taltech.inbankbackend.dto.DecisionResponse;
import ee.taltech.inbankbackend.entity.Segmentation;
import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecisionEngineTest {

    @InjectMocks
    private DecisionEngine decisionEngine;

    @Mock
    private LoanValidationService loanValidationService;

    @Mock
    private CreditScoringService creditScoringService;

    @Test
    void shouldReturnNoValidLoanForDebtorSegment() throws InvalidPersonalCodeException {
        // Arrange
        String debtorPersonalCode = "37605030299";
        DecisionRequest request = new DecisionRequest(debtorPersonalCode, 4000L, 12);

        doNothing().when(loanValidationService).validatePersonalCode(debtorPersonalCode);
        when(Segmentation.fromPersonalCode(debtorPersonalCode)).thenReturn(Segmentation.DEBT);

        // Act
        DecisionResponse response = decisionEngine.calculateApprovedLoan(request);

        // Assert
        assertResponse(response, null, null, "No valid loan found!");
    }

    @ParameterizedTest
    @CsvSource({
            "50307172740, SEG_ONE, 100, 4000, 12, true",
            "38411266610, SEG_TWO, 300, 4000, 12, true",
            "35006069515, SEG_THREE, 1000, 4000, 12, true",
            "50307172740, SEG_ONE, 100, 4000, 12, false"  // Loan rejected case
    })
    void shouldHandleValidSegmentsCorrectly(String personalCode, String segment, int creditModifier, Long loanAmount, int loanPeriod, boolean isApproved)
            throws InvalidPersonalCodeException {

        // Arrange
        DecisionRequest request = new DecisionRequest(personalCode, loanAmount, loanPeriod);

        doNothing().when(loanValidationService).validatePersonalCode(personalCode);
        when(Segmentation.fromPersonalCode(personalCode)).thenReturn(Segmentation.valueOf(segment));
        when(creditScoringService.isLoanApproved(creditModifier, loanAmount, loanPeriod)).thenReturn(isApproved);

        // Act
        DecisionResponse response = decisionEngine.calculateApprovedLoan(request);

        // Assert
        if (isApproved) {
            assertResponse(response, loanAmount, loanPeriod, null);
        } else {
            assertResponse(response, null, null, "No valid loan found!");
        }
    }

    @Test
    void shouldReturnErrorForInvalidPersonalCode() throws InvalidPersonalCodeException {
        // Arrange
        String invalidPersonalCode = "12345678901";
        DecisionRequest request = new DecisionRequest(invalidPersonalCode, 4000L, 12);

        doThrow(new InvalidPersonalCodeException("Invalid personal ID code!"))
                .when(loanValidationService).validatePersonalCode(invalidPersonalCode);

        // Act
        DecisionResponse response = decisionEngine.calculateApprovedLoan(request);

        // Assert
        assertResponse(response, null, null, "Invalid personal ID code!");
    }

    @Test
    void shouldReturnNoValidLoanIfRejectedByCreditScoring() throws InvalidPersonalCodeException {
        // Arrange
        String personalCode = "50307172740";
        DecisionRequest request = new DecisionRequest(personalCode, 4000L, 12);

        doNothing().when(loanValidationService).validatePersonalCode(personalCode);
        when(Segmentation.fromPersonalCode(personalCode)).thenReturn(Segmentation.SEG_ONE);
        when(creditScoringService.isLoanApproved(100, 4000L, 12)).thenReturn(false);

        // Act
        DecisionResponse response = decisionEngine.calculateApprovedLoan(request);

        // Assert
        assertResponse(response, null, null, "No valid loan found!");
    }

    // ✅ Utility method for clean assertions
    private void assertResponse(DecisionResponse response, Long expectedLoanAmount, Integer expectedLoanPeriod, String expectedErrorMessage) {
        assertEquals(expectedLoanAmount, response.loanAmount());
        assertEquals(expectedLoanPeriod, response.loanPeriod());
        assertEquals(expectedErrorMessage, response.errorMessage());
    }
}

