package com.example.blackwidow;

/**
 * Created by Bobak on 11/26/2017.
 * Object used to bind data to TextViews in the ExpandableListView option menu
 */

public class ListItem {
    private String label;
    private boolean selected;

    public ListItem(String label, boolean selected) {
        this.label = label;
        this.selected = selected;
    }

    public String getLabel() {
        return label;
    }

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelected() {
        selected = !selected;
    }

    public void setSelected(boolean newSelected) {
        selected = newSelected;
    }
}
