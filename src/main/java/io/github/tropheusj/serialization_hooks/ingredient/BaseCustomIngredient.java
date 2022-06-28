package io.github.tropheusj.serialization_hooks.ingredient;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public abstract class BaseCustomIngredient extends Ingredient implements CustomIngredient {
	public BaseCustomIngredient(Stream<? extends Value> stream) {
		super(stream);
	}

	public BaseCustomIngredient() {
		this(Stream.of());
	}
}
