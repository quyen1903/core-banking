package com.quinnbank.core.cif.api.annotation;

import com.quinnbank.core.cif.api.annotation.NullOrNotBlankValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented 
@Constraint(validatedBy = NullOrNotBlankValidator.class) //specifies the validator class that will be used to validate the annotated element
@Retention(RetentionPolicy.RUNTIME)//this annotation will be available at runtime
//this annotation can be applied to fields and parameters
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
//        ElementType.RECORD_COMPONENT,
//        ElementType.ANNOTATION_TYPE
})
public @interface NullOrNotBlank {
    //default error message if validation fails
    String message() default "must not be blank"; 

    //used for grouping constraints
    Class<?>[] groups() default {}; 

    //used to carry metadata information
    Class<? extends Payload>[] payload() default {}; 
}
