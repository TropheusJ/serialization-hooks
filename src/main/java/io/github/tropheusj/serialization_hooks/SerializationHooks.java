package io.github.tropheusj.serialization_hooks;

import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import io.github.tropheusj.serialization_hooks.value.ValueDeserializer;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializationHooks implements ModInitializer {
	public static final String ID = "serialization_hooks";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		IngredientDeserializer.init();
		ValueDeserializer.init();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
