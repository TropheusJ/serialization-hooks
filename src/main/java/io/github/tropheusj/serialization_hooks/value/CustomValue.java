package io.github.tropheusj.serialization_hooks.value;

import io.github.tropheusj.serialization_hooks.ingredient.CustomIngredient;
import net.minecraft.world.item.crafting.Ingredient.Value;

/**
 * A custom Value. Should be implemented onto all custom Values.
 */
public interface CustomValue {
	/**
	 * @return the custom ValueDeserializer used by this value, or null
	 * if vanilla deserialization should be used.
	 */
	ValueDeserializer getDeserializer();

	default boolean customDeserializer() {
		return getDeserializer() != null;
	}

	static boolean customDeserializer(Value value) {
		return value instanceof CustomValue c && c.customDeserializer();
	}
}
