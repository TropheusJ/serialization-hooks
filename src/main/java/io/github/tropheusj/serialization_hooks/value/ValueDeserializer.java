package io.github.tropheusj.serialization_hooks.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.tropheusj.serialization_hooks.SerializationHooks;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.Value;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A ValueDeserializer handles converting Json back to a Value.
 */
public interface ValueDeserializer {
	/**
	 * The Registry for serializers.
	 */
	Registry<ValueDeserializer> REGISTRY = FabricRegistryBuilder.createSimple(
			ValueDeserializer.class, SerializationHooks.id("value_deserializers")
	).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	/**
	 * List of deserializer IDs that are referenced in recipes but were not found.
	 * Used to avoid log spam.
	 */
	List<ResourceLocation> KNOWN_MISSING = new ArrayList<>();

	/**
	 * Create a Value from the given json object.
	 * This should reflect the corresponding {@link Value#serialize()} method in your Value.
	 */
	Ingredient.Value fromJson(JsonObject object);

	static void init() {
		// load the class and registry
	}

	/**
	 * Try to deserialize a Value from the given JsonObject.
	 * @return the deserialized value, or null if not custom
	 */
	@Nullable
	static Value tryDeserializeJson(JsonObject object) {
		JsonElement type = object.get("value_deserializer");
		if (type != null && type.isJsonPrimitive()) {
			ResourceLocation id = new ResourceLocation(type.getAsString());
			ValueDeserializer deserializer = ValueDeserializer.REGISTRY.get(id);
			if (deserializer != null)
				return deserializer.fromJson(object);
			if (KNOWN_MISSING.contains(id))
				return null;
			KNOWN_MISSING.add(id);
			SerializationHooks.LOGGER.error("ValueDeserializer with ID not found: [{}] This can be ignored unless recipes are missing or broken.", id);
		}
		return null;
	}
}
