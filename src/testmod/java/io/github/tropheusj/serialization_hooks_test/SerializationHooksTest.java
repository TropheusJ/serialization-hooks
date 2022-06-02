package io.github.tropheusj.serialization_hooks_test;

import io.github.tropheusj.serialization_hooks.IngredientDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class SerializationHooksTest implements ModInitializer {
	public static final String ID = "serialization_hooks_test";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	// todo: loom why
	@Override
	public void onInitialize() {
		IngredientDeserializer.init();
		Registry.register(IngredientDeserializer.REGISTRY, id("test"), new TestIngredientDeserializer());
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
