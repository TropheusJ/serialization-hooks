package io.github.tropheusj.serialization_hooks_test.value;

import com.google.gson.JsonObject;

import io.github.tropheusj.serialization_hooks.value.ValueDeserializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient.Value;

public class TestValueDeserializer implements ValueDeserializer {
	@Override
	public Value fromJson(JsonObject object) {
		Item item = Registry.ITEM.get(new ResourceLocation(object.get("item").getAsString()));
		System.out.println("deserialized custom value");
		return new AlwaysSpongeValue(new ItemStack(item));
	}
}
