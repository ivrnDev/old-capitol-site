package com.econnect.barangaymanagementapp.Utils;

import com.econnect.barangaymanagementapp.Enumeration.NavigationItems;

public class NavigationState {
    private NavigationItems activeItem;

    public NavigationItems getActiveItem() {
        return activeItem;
    }

    public void setActiveItem(NavigationItems activeItem) {
        this.activeItem = activeItem;
    }
}