package net.halalaboos.huzuni.api.mod;

/**
 * Holds a key code value and performs an action when pressed.
 * */
public interface Keybind {

    /**
     * Sets the key code.
     * */
	void setKeycode(int keyCode);

    /**
     * @return The keycode associated with this key bind.
     * */
	int getKeycode();

    /**
     * @return True if the key bind has a key code.
     * */
	boolean isBound();

    /**
     * @return The string name of the key code.
     * */
	String getKeyName();

    /**
     * @return True if the key is being pressed.
     * */
	boolean isPressed();

    /**
     * Invoked when pressed to perform an action.
     * */
	void pressed();
}
