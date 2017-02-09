package net.halalaboos.huzuni.api.settings;

/**
 * Mode which contains only state items and updates them when they are selected or deselected.
 * Created by Brandon on 10/6/2016.
 */
public class StateMode <I extends StateMode.StateItem> extends Mode <I> {

    public StateMode(String name, String description, I... items) {
        super(name, description, items);
    }

    @Override
    public void setSelectedItem(int selectedItem) {
        int oldItem = super.getSelected();
        super.setSelectedItem(selectedItem);
        if (oldItem != selectedItem) {
            getItems()[oldItem].onDeselect();
        }
        getItems()[selectedItem].onSelect();
    }

    /**
     * Basic implementation of the state item.
     * */
    public static abstract class BasicStateItem implements StateItem {

        private final String name, description;

        public BasicStateItem(String name, String description) {
            this.name = name;
            this.description = description;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

    /**
     * Nameable item which is updated when it's parent mode has selected or deselected it.
     * */
    public interface StateItem extends Nameable {

        void onSelect();

        void onDeselect();
    }
}
