package nivoridocs.flowstone.config.custom;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = { Exists.IdentifierValidator.class, Exists.OptionalIdentifierValidator.class })
public @interface Exists {

	String message() default "{nivoridocs.flowstone.config.Exists.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	public static class IdentifierValidator implements ConstraintValidator<Exists, Identifier> {

		@Override
		public boolean isValid(Identifier value, ConstraintValidatorContext context) {
			return Registry.BLOCK.containsId(value);
		}

	}
	
	public static class OptionalIdentifierValidator implements ConstraintValidator<Exists, Optional<Identifier>> {

		@Override
		public boolean isValid(Optional<Identifier> value, ConstraintValidatorContext context) {
			return value.map(Registry.BLOCK::containsId).orElse(true);
		}

	}

}
