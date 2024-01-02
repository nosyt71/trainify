package nus.iss.trainify.validation;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FocusValidator implements ConstraintValidator<Focus, String> {
    
    List<String> focus = Arrays.asList(
        "Shoulder", "Chest", "Abdominal", "Legs", "Full Body");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for (String string : focus) {
            if (value.equals(string)) return true;
        }
        return false;
    }

}