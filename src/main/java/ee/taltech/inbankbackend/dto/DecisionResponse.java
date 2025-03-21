package ee.taltech.inbankbackend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Holds the response data of the REST endpoint.
 */

public record DecisionResponse(Long loanAmount,Integer loanPeriod,String errorMessage){}

