package validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;


class ValidateAuthorizedPersonTest {

    @Test
    void validateAuthorizedPerson() {

        AuthorizedPerson person1 = new AuthorizedPerson("Jame", "Kok",
                "lol@gmail.com", "password",
                LocalDateTime.now().minus(5, ChronoUnit.MINUTES));

        AuthorizedPerson person2 = new AuthorizedPerson("Jame", "Kok",
                "lolo@gmail.com", "password",
                LocalDateTime.of(2021, 9, 15, 10, 20)); // one invalid fields

        AuthorizedPerson person3 = new AuthorizedPerson("Jame", "Kok",
                "lolanGmail.com", "password",
                LocalDateTime.of(2021, 9, 15, 10, 45)); // two invalid fields

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthorizedPerson>> allViolations = new HashSet<>();
        allViolations.addAll(validator.validate(person1));
        allViolations.addAll(validator.validate(person2));
        allViolations.addAll(validator.validate(person3));

        allViolations.forEach(x -> System.out.println(x.getMessage()));
        Assertions.assertEquals(3, allViolations.size());

    }

}