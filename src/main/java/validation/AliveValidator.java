package validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AliveValidator implements ConstraintValidator<Alive, LocalDateTime> {

    private long minutes;

    @Override
    public void initialize(Alive minutes) {
        this.minutes = minutes.value();
    }

    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext constraintValidatorContext) {
        if (dateTime == null) return false;
        return ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now()) < minutes;
    }
}
