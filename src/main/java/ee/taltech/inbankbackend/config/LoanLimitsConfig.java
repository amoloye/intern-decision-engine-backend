package ee.taltech.inbankbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "loan.limits")
public class LoanLimitsConfig {

    private int amountMin;
    private int amountMax;
    private int periodMin;
    private int periodMax;

    // Getters and Setters
    public int getAmountMin() { return amountMin; }
    public void setAmountMin(int amountMin) { this.amountMin = amountMin; }

    public int getAmountMax() { return amountMax; }
    public void setAmountMax(int amountMax) { this.amountMax = amountMax; }

    public int getPeriodMin() { return periodMin; }
    public void setPeriodMin(int periodMin) { this.periodMin = periodMin; }

    public int getPeriodMax() { return periodMax; }
    public void setPeriodMax(int periodMax) { this.periodMax = periodMax; }
}
