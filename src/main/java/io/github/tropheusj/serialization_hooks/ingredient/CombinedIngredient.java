package io.github.tropheusj.serialization_hooks.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.JsonSyntaxException;

import io.github.tropheusj.serialization_hooks.SerializationHooks;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An Ingredient that wraps several children Ingredients, matching if any of them match
 */
public class CombinedIngredient extends BaseCustomIngredient {
	public final List<Ingredient> children;
	private final ItemStack[] items;
	private final IntList stackingIds;
	private final boolean empty;

	public CombinedIngredient(List<Ingredient> children) {
		this.children = children;
		this.items = children.stream().flatMap(c -> Arrays.stream(c.getItems())).toArray(ItemStack[]::new);
		this.stackingIds = new IntArrayList(children.stream().flatMapToInt(c -> c.getStackingIds().intStream()).toArray());
		this.empty = children.stream().allMatch(Ingredient::isEmpty);
	}

	@Override
	public IngredientDeserializer getDeserializer() {
		return Deserializer.INSTANCE;
	}

	@Override
	public boolean test(@Nullable ItemStack itemStack) {
		for (Ingredient child : children) {
			if (child.test(itemStack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack[] getItems() {
		return items;
	}

	@Override
	public IntList getStackingIds() {
		return stackingIds;
	}

	@Override
	public boolean isEmpty() {
		return empty;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(Deserializer.ID);
		buffer.writeVarInt(children.size());
		for (Ingredient child : children) {
			child.toNetwork(buffer);
		}
	}

	@Override
	public JsonElement toJson() {
		JsonObject root = new JsonObject();
		root.addProperty("type", Deserializer.ID.toString());
		JsonArray children = new JsonArray();
		for (Ingredient child : this.children) {
			children.add(child.toJson());
		}
		root.add("children", children);
		return root;
	}

	public static class Deserializer implements IngredientDeserializer {
		public static final ResourceLocation ID = SerializationHooks.id("combined");
		public static final Deserializer INSTANCE = new Deserializer();

		@Override
		public Ingredient fromNetwork(FriendlyByteBuf buffer) {
			int count = buffer.readVarInt();
			List<Ingredient> children = new ArrayList<>(count);
			for (int i = 0; i < count; i++) {
				children.add(Ingredient.fromNetwork(buffer));
			}
			return new CombinedIngredient(children);
		}

		@Override
		public Ingredient fromJson(JsonObject object) {
			JsonElement childrenElement = object.get("children");
			if (!(childrenElement instanceof JsonArray array)) {
				throw new JsonSyntaxException("CombinedIngredient expected JSON element to be an array, found: " + childrenElement);
			}
			List<Ingredient> children = new ArrayList<>(array.size());
			for (int i = 0; i < array.size(); i++) {
				children.add(Ingredient.fromJson(array.get(i)));
			}
			return new CombinedIngredient(children);
		}
	}
}
