package ee.taltech.inbankbackend.service;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanValidationService {

    private final EstonianPersonalCodeValidator validator;

    public void validatePersonalCode(String personalCode) throws InvalidPersonalCodeException {
        if (!validator.isValid(personalCode)) {
            throw new InvalidPersonalCodeException("Invalid personal ID code!");
        }
    }
}
