package io.github.tropheusj.serialization_hooks.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import com.google.gson.JsonSyntaxException;

import io.github.tropheusj.serialization_hooks.IngredientDeserializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Ingredient.class, priority = 547) // random magic number to get us applied earlier
public abstract class IngredientMixin {
	@Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
	private static void serialization_hooks$fromNetwork(FriendlyByteBuf buffer, CallbackInfoReturnable<Ingredient> cir) {
		ResourceLocation id = buffer.readResourceLocation();
		if (!id.equals(IngredientDeserializer.NONE)) {
			IngredientDeserializer deserializer = IngredientDeserializer.REGISTRY.get(id);
			if (deserializer == null)
				throw new IllegalStateException("[SerializationHooks] IngredientSerializer with ID not found: " + id);
			cir.setReturnValue(deserializer.fromPacket(buffer));
		}
	}

	@Inject(method = "fromJson", at = @At("HEAD"), cancellable = true)
	private static void serialization_hooks$fromJsonObject(JsonElement json, CallbackInfoReturnable<Ingredient> cir) {
		if (json == null || json.isJsonNull()) {
			throw new JsonSyntaxException("Item cannot be null");
		}
		for (IngredientDeserializer deserializer : IngredientDeserializer.REGISTRY) {
			Ingredient deserialized = deserializer.fromJson(json);
			if (deserialized == null)
				continue;
			cir.setReturnValue(deserialized);
		}
	}
}
