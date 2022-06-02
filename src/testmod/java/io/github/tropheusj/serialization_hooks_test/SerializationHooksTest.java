package io.github.tropheusj.serialization_hooks_test;

import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;

import io.github.tropheusj.serialization_hooks.value.ValueDeserializer;
import io.github.tropheusj.serialization_hooks_test.ingredient.TestIngredientDeserializer;

import io.github.tropheusj.serialization_hooks_test.value.TestValueDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class SerializationHooksTest implements ModInitializer {
	public static final String ID = "serialization_hooks_test";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		IngredientDeserializer.init();
		Registry.register(IngredientDeserializer.REGISTRY, id("test"), new TestIngredientDeserializer());
		Registry.register(ValueDeserializer.REGISTRY, id("always_sponge"), new TestValueDeserializer());
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
