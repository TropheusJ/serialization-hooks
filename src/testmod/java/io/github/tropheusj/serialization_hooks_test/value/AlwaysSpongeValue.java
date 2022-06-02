package io.github.tropheusj.serialization_hooks_test.value;

import com.google.gson.JsonObject;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Same as ItemValue, but always includes a Sponge as well.
 */
public class AlwaysSpongeValue extends ItemValue {
	public AlwaysSpongeValue(ItemStack itemStack) {
		super(itemStack);
	}

	@Override
	public Collection<ItemStack> getItems() {
		Collection<ItemStack> items = new ArrayList<>(super.getItems());
		items.add(Items.SPONGE.getDefaultInstance());
		return items;
	}

	@Override
	public JsonObject serialize() {
		JsonObject json = super.serialize();
		json.addProperty("value_deserializer", "always_sponge");
		return json;
	}
}
