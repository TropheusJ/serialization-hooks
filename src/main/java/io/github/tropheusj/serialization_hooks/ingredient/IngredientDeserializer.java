package io.github.tropheusj.serialization_hooks.ingredient;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import io.github.tropheusj.serialization_hooks.SerializationHooks;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An IngredientDeserializer handles turning packets and Json back into actual Ingredients.
 */
public interface IngredientDeserializer {
	/**
	 * The Registry for deserializers.
	 */
	Registry<IngredientDeserializer> REGISTRY = FabricRegistryBuilder.createSimple(
			IngredientDeserializer.class, SerializationHooks.id("ingredient_deserializers")
	).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	/**
	 * List of deserializer IDs that are referenced in recipes but were not found.
	 * Used to avoid log spam.
	 */
	List<ResourceLocation> KNOWN_MISSING = new ArrayList<>();

	/**
	 * The ID representing no deserializer.
	 */
	ResourceLocation NONE = SerializationHooks.id("no_deserializer");

	/**
	 * Create an Ingredient from the packet.
	 * This should reflect the corresponding {@link Ingredient#toNetwork(FriendlyByteBuf)} method in your Ingredient.
	 */
	Ingredient fromNetwork(FriendlyByteBuf buffer);

	/**
	 * Create an Ingredient from the given json object.
	 * This should reflect the corresponding {@link Ingredient#toJson()} method in your Ingredient.
	 */
	Ingredient fromJson(JsonObject object);

	static void init() {
		// load the class and registry
	}

	/**
	 * Try to deserialize an Ingredient from the given JsonObject.
	 * @return the deserialized ingredient, or null if not custom
	 */
	@Nullable
	static Ingredient tryDeserializeJson(JsonObject object) {
		JsonElement type = object.get("type");
		if (type != null && type.isJsonPrimitive()) {
			ResourceLocation deserializerId = new ResourceLocation(type.getAsString());
			IngredientDeserializer deserializer = IngredientDeserializer.REGISTRY.get(deserializerId);
			if (deserializer == null)
				throw new IllegalStateException("Ingredient deserializer with ID not found: " + deserializerId);
			return deserializer.fromJson(object);
		}
		return null;
	}

	/**
	 * Try to deserialize an Ingredient from the given buffer.
	 * @return the deserialized ingredient, or null if not custom
	 */
	@Nullable
	static Ingredient tryDeserializeNetwork(FriendlyByteBuf buf) {
		int readIndex = buf.readerIndex();
		try {
			ResourceLocation id = buf.readResourceLocation();
			if (!id.equals(IngredientDeserializer.NONE)) {
				IngredientDeserializer serializer = IngredientDeserializer.REGISTRY.get(id);
				if (serializer != null)
					return serializer.fromNetwork(buf);
				if (KNOWN_MISSING.contains(id))
					return null;
				KNOWN_MISSING.add(id);
				SerializationHooks.LOGGER.error("IngredientDeserializer with ID not found: [{}] This can be ignored unless recipes are missing or broken.", id);
			}
		} catch (Exception e) {
			buf.readerIndex(readIndex);
		}
		return null;
	}
}
