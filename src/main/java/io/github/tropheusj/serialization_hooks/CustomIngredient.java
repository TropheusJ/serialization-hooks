package io.github.tropheusj.serialization_hooks;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * This interface should be implemented onto all custom Ingredients.
 * This is required for your ingredient to function correctly.
 */
public interface CustomIngredient {
	/**
	 * @return the custom IngredientDeserializer used by this ingredient, or null
	 * if vanilla deserialization should be used.
	 */
	IngredientDeserializer getDeserializer();

	/**
	 * @return an array of Ingredient.Values, created from this Ingredient.
	 */
	Ingredient.Value[] getValues();

	default boolean customDeserializer() {
		return getDeserializer() != null;
	}

	static boolean customDeserializer(Ingredient ingredient) {
		return ingredient instanceof CustomIngredient c && c.customDeserializer();
	}

	static Ingredient.Value[] getValues(Ingredient ingredient) {
		return ingredient instanceof CustomIngredient c ? c.getValues() : ingredient.values;
	}

	static Stream<Ingredient.Value> streamValues(Ingredient ingredient) {
		return Arrays.stream(getValues(ingredient));
	}
}
