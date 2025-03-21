package ee.taltech.inbankbackend.service;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.dto.DecisionRequest;
import ee.taltech.inbankbackend.dto.DecisionResponse;
import ee.taltech.inbankbackend.entity.Segmentation;
import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import ee.taltech.inbankbackend.exceptions.NoValidLoanException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service class that provides a method for calculating an approved loan amount and period for a customer.
 * The loan amount is calculated based on the customer's credit modifier,
 * which is determined by the last four digits of their ID code.
 */
@Service
@RequiredArgsConstructor
public class DecisionEngine {

    private final LoanValidationService loanValidationService;
    private final CreditScoringService creditScoringService;

    public DecisionResponse calculateApprovedLoan(DecisionRequest request) throws InvalidPersonalCodeException {
        // Validate the personal code
        loanValidationService.validatePersonalCode(request.personalCode());

        // Determine the segmentation based on the personal code
        Segmentation segmentation = Segmentation.fromPersonalCode(request.personalCode());

        // If the customer is in DEBT segment, no loan can be approved
        if (segmentation == Segmentation.DEBT) {
            return new DecisionResponse(null, null, "No valid loan found!");
        }

        int creditModifier = segmentation.getValue();
        Long loanAmount = request.loanAmount();  // No need for casting
        int loanPeriod = request.loanPeriod();

        // Apply credit scoring algorithm
        boolean isApproved = creditScoringService.isLoanApproved(creditModifier, loanAmount, loanPeriod);

        if (!isApproved) {
            return new DecisionResponse(null, null, "No valid loan found!");
        }

        // Loan is approved, return response
        return new DecisionResponse(loanAmount, loanPeriod, null);
    }
}

