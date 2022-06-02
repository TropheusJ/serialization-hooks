package io.github.tropheusj.serialization_hooks_test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TestIngredientDeserializer implements IngredientDeserializer {
	@Override
	public Ingredient fromPacket(FriendlyByteBuf buffer) {
		return Ingredient.of(buffer.readItem());
	}

	@Override
	public Ingredient fromJsonObject(JsonObject object) {
		ResourceLocation id = new ResourceLocation(object.get("a").getAsString());
		System.out.println("deserialized " + id);
		return Ingredient.of(Registry.ITEM.get(id));
	}

	@Nullable
	@Override
	public Ingredient fromJsonArray(JsonArray array) {
		return Ingredient.of(StreamSupport.stream(array.spliterator(), false)
				.flatMap(element -> {
					JsonObject obj = element.getAsJsonObject();
					Item one = Registry.ITEM.get(new ResourceLocation(obj.get("one").getAsString()));
					Item two = Registry.ITEM.get(new ResourceLocation(obj.get("two").getAsString()));
					return Stream.of(one, two);
				}).map(ItemStack::new));
	}
}
