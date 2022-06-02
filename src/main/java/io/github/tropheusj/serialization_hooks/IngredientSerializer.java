package io.github.tropheusj.serialization_hooks;

import com.google.gson.JsonArray;

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
public interface IngredientSerializer {
	/**
	 * The Registry for serializers.
	 */
	Registry<IngredientSerializer> REGISTRY = FabricRegistryBuilder.createSimple(
			IngredientSerializer.class, SerializationHooks.id("ingredient_serializers")
	).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	/**
	 * The ID representing no serializer.
	 */
	ResourceLocation NONE = SerializationHooks.id("no_serializer");

	/**
	 * Create an Ingredient from the packet.
	 * This buffer is guaranteed to be designed for this serializer, and this method should
	 * always return non-null.
	 */
	Ingredient fromPacket(FriendlyByteBuf buffer);

	/**
	 * Create an Ingredient from the json array.
	 * This object is guaranteed to be designed for this serializer, and this method should
	 * always return non-null.
	 */
	Ingredient fromJsonObject(JsonObject object);

	/**
	 * Create an Ingredient from the json array.
	 * Unlike {@link IngredientSerializer#fromJsonObject(JsonObject)}, this array is not guaranteed to be
	 * designed for this serializer. This is because array-based ingredients have no way to declare their serializer.
	 * Serializers should attempt to deserialize, and return null if they can't.
	 */
	@Nullable
	Ingredient fromJsonArray(JsonArray array);

	static void init() {
		// load the class and registry
	}
}
