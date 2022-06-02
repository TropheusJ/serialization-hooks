package io.github.tropheusj.serialization_hooks;

import com.google.gson.JsonElement;

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
	Ingredient fromPacket(FriendlyByteBuf buffer);

	/**
	 * Create an Ingredient from the given json element.
	 * @return null if no Ingredient could be deserialized
	 */
	@Nullable
	Ingredient fromJson(JsonElement object);

	static void init() {
		// load the class and registry
	}
}
