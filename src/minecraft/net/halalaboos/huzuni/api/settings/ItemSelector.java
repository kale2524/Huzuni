package net.halalaboos.huzuni.api.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.halalaboos.huzuni.api.gui.components.tree.ItemSelectorComponent;
import net.halalaboos.huzuni.api.gui.components.tree.TreeComponentFactory;
import net.minecraft.item.ItemStack;

/**
 * Node which contains a list of item data. Each item data that is enabled is saved.
 * */
public class ItemSelector <T> extends Node {

	private final List<ItemData<T>> itemDatas = new ArrayList<ItemData<T>>();
	
	public ItemSelector(String name, String description) {
		super(name, description);
		TreeComponentFactory.addComponent(ItemSelector.class, ItemSelectorComponent.class);
	}

	/**
	 * @return True if this node contains an item data which is enabled and contains the given object as one of it's types.
	 * */
	public boolean isEnabledObject(T object) {
		for (ItemData<T> itemData : itemDatas) {
			if (itemData.isEnabled()) {
				for (T itemObject : itemData.types) {
					if (object.equals(itemObject))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Adds an item data with the item stack and array of types.
	 * */
	public ItemData<T> addItem(ItemStack item, T... types) {
		ItemData<T> itemData = new ItemData<T>(item, types);
		itemDatas.add(itemData);
		return itemData;
	}
	
	public List<ItemData<T>> getItemDatas() {
		return itemDatas;
	}

	@Override
	public boolean isObject(JsonObject object) {
		return object.getAsJsonArray(getName()) != null;
	}

	@Override
	public void save(JsonObject object) throws IOException {
		super.save(object);
		JsonArray array = new JsonArray();
		for (int i = 0; i < itemDatas.size(); i++) {
			if (itemDatas.get(i).isEnabled()) {
				JsonObject itemObject = new JsonObject();
				itemObject.addProperty("id", i);
				array.add(itemObject);
			}
		}
		object.add(getName(), array);
	}

	@Override
	public void load(JsonObject object) throws IOException {
		super.load(object);
		if (isObject(object)) {
			JsonArray values = object.get(getName()).getAsJsonArray();
			for (JsonElement element : values) {
				JsonObject itemObject = (JsonObject) element;
				
				int id = itemObject.get("id").getAsInt();
				if (id >= 0 && id < itemDatas.size())
					itemDatas.get(id).setEnabled(true);
			}
		}
	}

	/**
	 * Pairs an item stack to an array of data. Can be set 'enabled' or 'disabled'.
	 * */
	public static class ItemData <I> {
		
		public final ItemStack item;
		
		private final I[] types;
		
		private boolean enabled = false;
		
		@SafeVarargs
		public ItemData(ItemStack item, I... types) {
			this.item = item;
			this.types = types;
		}
		
		public void toggle() {
			enabled = !enabled;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public I[] getTypes() {
			return types;
		}
	}
	
}
