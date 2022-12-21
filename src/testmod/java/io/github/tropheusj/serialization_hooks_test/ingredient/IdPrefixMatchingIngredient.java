package io.github.tropheusj.serialization_hooks_test.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.tropheusj.serialization_hooks.ingredient.BaseCustomIngredient;
import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import io.github.tropheusj.serialization_hooks_test.SerializationHooksTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map.Entry;

/**
 * An Ingredient that matches all items whose ID starts with the provided string.
 */
public class IdPrefixMatchingIngredient extends BaseCustomIngredient {
	public final List<Item> matches;
	public final String match;

	public IdPrefixMatchingIngredient(String match) {
		this.match = match;
		matches = BuiltInRegistries.ITEM.entrySet().stream().filter(e -> e.getKey().location().getPath().startsWith(match)).map(Entry::getValue).toList();
	}

	@Override
	public boolean test(@Nullable ItemStack itemStack) {
		return itemStack != null && matches.contains(itemStack.getItem());
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(Deserializer.ID);
		buffer.writeUtf(match);
	}

	@Override
	public JsonElement toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("match", this.match);
		return obj;
	}

	@Override
	public IngredientDeserializer getDeserializer() {
		return Deserializer.INSTANCE;
	}

	public static class Deserializer implements IngredientDeserializer {
		public static final ResourceLocation ID = SerializationHooksTest.id("id_prefix");
		public static final Deserializer INSTANCE = new Deserializer();

		@Override
		public Ingredient fromNetwork(FriendlyByteBuf buffer) {
			String match = buffer.readUtf();
			return new IdPrefixMatchingIngredient(match);
		}

		@Override
		public Ingredient fromJson(JsonObject object) {
			String match = GsonHelper.getAsString(object, "match");
			return new IdPrefixMatchingIngredient(match);
		}
	}
}
