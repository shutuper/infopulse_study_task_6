package validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AliveValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Repeatable(Alive.List.class)
public @interface Alive {

    String message() default "Must be less than {value} minutes";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long value(); // in minutes

    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Alive[] value();
    }

}



