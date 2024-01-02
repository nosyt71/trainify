package nus.iss.trainify.validation;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LocationValidator implements ConstraintValidator<Location, String> {
    
    List<String> location = Arrays.asList(
        "Home", "Gym");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for (String string : location) {
            if (value.equals(string)) return true;
        }
        return false;
    }

}