package io.github.tropheusj.serialization_hooks.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import com.google.gson.JsonSyntaxException;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.tropheusj.serialization_hooks.ingredient.CombinedIngredient;
import io.github.tropheusj.serialization_hooks.ingredient.CustomIngredient;
import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import io.github.tropheusj.serialization_hooks.value.ValueDeserializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
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
import java.util.Arrays;
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

	@ModifyReturnValue(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
	private boolean serialization_hooks$customTesting(boolean itemMatches, @Nullable ItemStack itemStack) {
		if (this instanceof CustomIngredient custom && custom.customTest()) {
			return custom.testCustom(itemStack, itemMatches);
		}
		return itemMatches;
	}

	@Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
	private static void serialization_hooks$fromNetwork(FriendlyByteBuf buffer, CallbackInfoReturnable<Ingredient> cir) {
		Ingredient deserialized = IngredientDeserializer.tryDeserializeNetwork(buffer);
		if (deserialized != null)
			cir.setReturnValue(deserialized);
	}

	/**
	 * @author Tropheus Jay
	 * @reason to make JsonArray handling recursive, add custom object deserialization, and fast-fail in conflicts
	 */
	@Overwrite
	public static Ingredient fromJson(@Nullable JsonElement json, boolean allowAir) {
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
			if (jsonArray.size() == 0 && !allowAir) {
				throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
			} else {
				List<Ingredient> nested = new ArrayList<>();
				for (JsonElement element : jsonArray) {
					nested.add(fromJson(element, allowAir));
				}
				// use vanilla method for vanilla ingredients
				if (nested.stream().allMatch(i -> i.getClass() == Ingredient.class)) {
					return fromValues(nested.stream().flatMap(i -> Arrays.stream(i.values)));
				} else {
					// custom ingredients require custom handling
					return new CombinedIngredient(nested);
				}
			}
		} else {
			throw new JsonSyntaxException("Expected item to be object or array of objects");
		}
	}

	@Inject(method = "valueFromJson", at = @At("HEAD"), cancellable = true)
	private static void serialization_hooks$valueFromJson(JsonObject json, CallbackInfoReturnable<Value> cir) {
		Value deserialized = ValueDeserializer.tryDeserializeJson(json);
		if (deserialized != null)
			cir.setReturnValue(deserialized);
	}
}
