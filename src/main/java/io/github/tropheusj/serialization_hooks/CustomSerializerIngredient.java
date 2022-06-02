package io.github.tropheusj.serialization_hooks;

/**
 * An Ingredient with a custom IngredientSerializer.
 */
public interface CustomSerializerIngredient {
	IngredientDeserializer getSerializer();
}
