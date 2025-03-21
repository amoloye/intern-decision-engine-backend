package ee.taltech.inbankbackend.service;

import org.springframework.stereotype.Service;

@Service
public class CreditScoringService {

    public boolean isLoanApproved(int creditModifier, long loanAmount, int loanPeriod) {
        double creditScore = ((double) creditModifier / loanAmount * loanPeriod) / 10;
        return creditScore >= 0.1;
    }
}


