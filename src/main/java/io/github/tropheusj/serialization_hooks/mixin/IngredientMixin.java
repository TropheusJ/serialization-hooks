package io.github.tropheusj.serialization_hooks.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import io.github.tropheusj.serialization_hooks.IngredientDeserializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Ingredient.class, priority = 547) // random magic number to get us applied earlier
public abstract class IngredientMixin {
	@Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
	private static void serialization_hooks$fromNetwork(FriendlyByteBuf buffer, CallbackInfoReturnable<Ingredient> cir) {
		ResourceLocation id = buffer.readResourceLocation();
		if (!id.equals(IngredientDeserializer.NONE)) {
			IngredientDeserializer serializer = IngredientDeserializer.REGISTRY.get(id);
			if (serializer == null)
				throw new IllegalStateException("[SerializationHooks] IngredientSerializer with ID not found: " + id);
			cir.setReturnValue(serializer.fromPacket(buffer));
		}
	}

	@Inject(
			method = "fromJson",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/gson/JsonElement;isJsonObject()Z",
					shift = Shift.BY,
					by = 2
			),
			cancellable = true
	)
	private static void serialization_hooks$fromJsonObject(JsonElement json, CallbackInfoReturnable<Ingredient> cir) {
		JsonObject obj = json.getAsJsonObject();
		JsonElement serializerElement = obj.get("serializer");
		if (serializerElement == null)
			return;
		if (!serializerElement.isJsonPrimitive())
			throw new IllegalStateException("[SerializationHooks] A serializer is declared, but it is not a primitive: " + serializerElement);
		ResourceLocation id = new ResourceLocation(serializerElement.getAsString());
		IngredientDeserializer serializer = IngredientDeserializer.REGISTRY.get(id);
		if (serializer == null)
			throw new IllegalStateException("[SerializationHooks] IngredientSerializer with ID not found: " + id);
		cir.setReturnValue(serializer.fromJsonObject(obj));
	}

	@Inject(
			method = "fromJson",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/gson/JsonElement;isJsonArray()Z",
					shift = Shift.BY,
					by = 2
			),
			cancellable = true
	)
	private static void serialization_hooks$fromJsonArray(JsonElement json, CallbackInfoReturnable<Ingredient> cir) {
		JsonArray array = json.getAsJsonArray();
		for (IngredientDeserializer serializer : IngredientDeserializer.REGISTRY) {
			Ingredient deserialized = serializer.fromJsonArray(array);
			if (deserialized == null)
				continue;
			cir.setReturnValue(deserialized);
		}
	}
}
