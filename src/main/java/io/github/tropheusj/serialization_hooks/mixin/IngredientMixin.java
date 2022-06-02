package io.github.tropheusj.serialization_hooks.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import com.google.gson.JsonSyntaxException;

import io.github.tropheusj.serialization_hooks.CustomIngredient;
import io.github.tropheusj.serialization_hooks.IngredientDeserializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraft.world.item.crafting.Ingredient.Value;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mixin(value = Ingredient.class, priority = 547) // random magic number to get us applied earlier
public abstract class IngredientMixin {
	@Shadow
	public static Ingredient fromValues(Stream<? extends Value> stream) {
		return null;
	}

	@Shadow
	public static Value valueFromJson(JsonObject json) {
		return null;
	}

	@Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
	private static void serialization_hooks$fromNetwork(FriendlyByteBuf buffer, CallbackInfoReturnable<Ingredient> cir) {
		ResourceLocation id = buffer.readResourceLocation();
		if (!id.equals(IngredientDeserializer.NONE)) {
			IngredientDeserializer serializer = IngredientDeserializer.REGISTRY.get(id);
			if (serializer == null)
				throw new IllegalStateException("[SerializationHooks] IngredientSerializer with ID not found: " + id);
			cir.setReturnValue(serializer.fromNetwork(buffer));
		}
	}

	/**
	 * @author Tropheus Jay
	 * @reason to make JsonArray handling recursive, add custom object deserialization, and fast-fail in conflicts
	 */
	@Overwrite
	public static Ingredient fromJson(@Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
			throw new JsonSyntaxException("Item cannot be null");
		} else if (json.isJsonObject()) {
			JsonObject obj = json.getAsJsonObject();
			Ingredient deserialized = IngredientDeserializer.tryDeserializeJson(obj);
			if (deserialized != null)
				return deserialized;
			return fromValues(Stream.of(valueFromJson(obj)));
		} else if (json.isJsonArray()) {
			JsonArray jsonArray = json.getAsJsonArray();
			if (jsonArray.size() == 0) {
				throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
			} else {
				List<Ingredient> nested = new ArrayList<>();
				for (JsonElement element : jsonArray) {
					nested.add(fromJson(element));
				}
				return fromValues(nested.stream().flatMap(CustomIngredient::streamValues));
			}
		} else {
			throw new JsonSyntaxException("Expected item to be object or array of objects");
		}
	}

}