package nivoridocs.flowstone.config;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class IdentifierExistsValidator implements ConstraintValidator<Exists, Identifier> {

	@Override
	public boolean isValid(Identifier value, ConstraintValidatorContext context) {
		return Registry.BLOCK.containsId(value);
	}

}
