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
			ResourceLocation deserializerId = new ResourceLocation(type.getAsString());
			ValueDeserializer deserializer = ValueDeserializer.REGISTRY.get(deserializerId);
			if (deserializer == null)
				throw new IllegalStateException("Value deserializer with ID not found: " + deserializerId);
			return deserializer.fromJson(object);
		}
		return null;
	}
}
