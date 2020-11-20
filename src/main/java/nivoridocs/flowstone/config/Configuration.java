package nivoridocs.flowstone.config;

import java.util.Collection;
import java.util.Optional;

import net.minecraft.util.Identifier;

public interface Configuration {

	double getMinChance();

	double getMaxChance();

	int getBlocksLimit();

	Collection<? extends Item> getItems();

	public interface Item {

		Identifier getOre();

		Optional<Identifier> getBlock();

	}

}
