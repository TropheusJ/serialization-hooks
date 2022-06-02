package io.github.tropheusj.serialization_hooks;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class BaseCustomIngredient extends Ingredient implements CustomIngredient {
	public BaseCustomIngredient(Stream<? extends Value> stream) {
		super(stream);
	}

	@Override
	public IngredientDeserializer getDeserializer() {
		return null;
	}

	@Override
	public Value[] getValues() {
		return this.values;
	}
}
