package io.github.tropheusj.serialization_hooks.ingredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

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
	 * @return if this Ingredient should have custom logic for testing if an ItemStack matches it
	 */
	default boolean customTest() {
		return false;
	}

	/**
	 * Convenience method for adding custom logic for matching.
	 * In order for this to be used, {@link #customTest()} must return true.
	 * @param itemMatches if true, the given ItemStack's Item matches this Ingredient already.
	 * @return if the given ItemStack matches this Ingredient
	 */
	default boolean testCustom(@Nullable ItemStack stack, boolean itemMatches) {
		throw new IllegalStateException("May never be called if customTest return false");
	}

	default boolean customDeserializer() {
		return getDeserializer() != null;
	}

	static boolean customDeserializer(Ingredient ingredient) {
		return ingredient instanceof CustomIngredient c && c.customDeserializer();
	}
}
