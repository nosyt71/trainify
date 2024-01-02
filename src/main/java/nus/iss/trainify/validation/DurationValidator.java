package nus.iss.trainify.validation;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DurationValidator implements ConstraintValidator<Duration, String> {
    
    List<String> scores = Arrays.asList(
        "Home", "Gym");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for (String string : scores) {
            if (value.equals(string)) return true;
        }
        return false;
    }

}