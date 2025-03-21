package ee.taltech.inbankbackend.endpoint;

import ee.taltech.inbankbackend.dto.DecisionRequest;
import ee.taltech.inbankbackend.dto.DecisionResponse;
import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import ee.taltech.inbankbackend.exceptions.NoValidLoanException;
import ee.taltech.inbankbackend.service.DecisionEngine;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan")
@CrossOrigin
@RequiredArgsConstructor
public class DecisionEngineController {

    private final DecisionEngine decisionEngine;

    /**
     * Handles loan decision requests.
     *
     * @param request The request containing personal ID code, loan amount, and loan period
     * @return ResponseEntity with a DecisionResponse
     */
    @PostMapping("/decision")
    public ResponseEntity<DecisionResponse> requestDecision(@Valid @RequestBody DecisionRequest request) {
        try {
            return ResponseEntity.ok(decisionEngine.calculateApprovedLoan(request));
        } catch (InvalidPersonalCodeException e) {
            return ResponseEntity.badRequest().body(new DecisionResponse(null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new DecisionResponse(null, null, "An unexpected error occurred"));
        }
    }
}


