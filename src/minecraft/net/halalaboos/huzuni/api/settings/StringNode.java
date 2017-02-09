package net.halalaboos.huzuni.api.settings;

import java.io.IOException;

import com.google.gson.JsonObject;

/**
 * Node that consists of a single string. That's it.
 * */
public class StringNode extends Node {
		
	private final String defaultText;
	
	private String text;
	
	public StringNode(String name, String defaultText, String description) {
		super(name, description);
		this.text = defaultText;
		this.defaultText = defaultText;
	}
	
	@Override
	public void save(JsonObject object) throws IOException {
		super.save(object);
		object.addProperty(getName(), text);
	}

	@Override
	public void load(JsonObject object) throws IOException {
		super.load(object);
		if (isObject(object)) {
			text = object.get(getName()).getAsString();
		}
	}

	public String getDefaultText() {
		return defaultText;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public boolean hasText() {
		return !text.isEmpty();
	}

}
