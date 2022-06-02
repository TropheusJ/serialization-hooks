package io.github.tropheusj.serialization_hooks;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

/**
 * An IngredientSerializer handles converting between packets, json, and actual Ingredients.
 */
public interface IngredientDeserializer {
	/**
	 * The Registry for serializers.
	 */
	Registry<IngredientDeserializer> REGISTRY = FabricRegistryBuilder.createSimple(
			IngredientDeserializer.class, SerializationHooks.id("ingredient_deserializers")
	).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	/**
	 * The ID representing no serializer.
	 */
	ResourceLocation NONE = SerializationHooks.id("no_serializer");

	/**
	 * Create an Ingredient from the packet.
	 * This should reflect the corresponding {@link Ingredient#toNetwork(FriendlyByteBuf)} method in your Ingredient.
	 */
	Ingredient fromNetwork(FriendlyByteBuf buffer);

	/**
	 * Create an Ingredient from the given json object.
	 * This should reflect the corresponding {@link Ingredient#toJson()} method in your Ingredient.
	 */
	@Nullable
	Ingredient fromJson(JsonObject object);

	static void init() {
		// load the class and registry
	}

	/**
	 * Try to deserialize an Ingredient from the given JsonObject.
	 * @return deserialize ingredient, or null if not custom
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
}
