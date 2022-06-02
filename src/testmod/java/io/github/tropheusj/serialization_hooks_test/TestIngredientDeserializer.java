package io.github.tropheusj.serialization_hooks_test;

import com.google.gson.JsonObject;

import io.github.tropheusj.serialization_hooks.IngredientDeserializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.Nullable;

public class TestIngredientDeserializer implements IngredientDeserializer {
	public Ingredient fromNetwork(FriendlyByteBuf buffer) {
		return Ingredient.of(buffer.readItem());
	}

	@Nullable
	@Override
	public Ingredient fromJson(JsonObject object) {
		int test1 = object.get("test1").getAsInt();
		String stringTest = object.get("string_test").getAsString();
		boolean sponge = object.get("sponge").getAsBoolean();
		return Ingredient.of(sponge ? Items.SPONGE : Items.COAL_ORE);
	}
}
