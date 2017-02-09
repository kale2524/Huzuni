package net.halalaboos.huzuni.api.gui.widget;

/**
 * Formats the x and y positions of widgets.
 * */
public interface Glue {
	
	boolean modifiesX();
	
	boolean modifiesY();
	
	boolean isRight();
	
	boolean isLeft();
	
	boolean isTop();
	
	boolean isBottom();
	
	boolean isCenterX();
	
	boolean isCenterY();

	/**
	 * Formats the widget's x position based on this glue.
	 * */
	void formatX(Widget widget);

    /**
     * Formats the widget's y position based on this glue.
     * */
	void formatY(Widget widget);
	
}
