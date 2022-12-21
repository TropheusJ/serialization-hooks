package io.github.tropheusj.serialization_hooks.ingredient;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import com.google.gson.JsonSyntaxException;

import io.github.tropheusj.serialization_hooks.SerializationHooks;
import io.github.tropheusj.serialization_hooks.ingredient.CombinedIngredient.Deserializer;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.Nullable;

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
		Registry.register(REGISTRY, Deserializer.ID, Deserializer.INSTANCE);
	}

	/**
	 * Try to deserialize an Ingredient from the given JsonObject.
	 * @return the deserialized ingredient, or null if not custom
	 */
	@Nullable
	static Ingredient tryDeserializeJson(JsonObject object) {
		JsonElement type = object.get("type");
		if (type != null && type.isJsonPrimitive()) {
			ResourceLocation id = ResourceLocation.tryParse(type.getAsString());
			if (id == null)
				return null;
			IngredientDeserializer deserializer = IngredientDeserializer.REGISTRY.get(id);
			if (deserializer != null) {
				try {
					return deserializer.fromJson(object);
				} catch (JsonSyntaxException ex) {
					SerializationHooks.LOGGER.error("Failed to deserialize Ingredient using deserializer [{}]: {}", id, ex.getMessage());
				}
			}
			if (KNOWN_MISSING.contains(id))
				return null;
			KNOWN_MISSING.add(id);
			SerializationHooks.LOGGER.error("IngredientDeserializer with ID not found: [{}] this can be ignored unless issues occur.", id);
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
			ResourceLocation id = ResourceLocation.tryParse(buf.readUtf());
			if (id != null && !id.getPath().isEmpty()) {
				IngredientDeserializer deserializer = IngredientDeserializer.REGISTRY.get(id);
				if (deserializer != null)
					return deserializer.fromNetwork(buf);
				if (KNOWN_MISSING.contains(id))
					return null;
				KNOWN_MISSING.add(id);
				SerializationHooks.LOGGER.error("IngredientDeserializer with ID not found: [{}] this can be ignored unless issues occur.", id);
			}
			buf.readerIndex(readIndex);
			return null;
		} catch (DecoderException e) { // not a string
			buf.readerIndex(readIndex);
			return null;
		}
	}
}
