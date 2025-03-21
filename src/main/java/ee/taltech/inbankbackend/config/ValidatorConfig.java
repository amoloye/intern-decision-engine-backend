package ee.taltech.inbankbackend.config;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfig {
    @Bean
    public EstonianPersonalCodeValidator estonianPersonalCodeValidator() {
        return new EstonianPersonalCodeValidator();
    }
}
