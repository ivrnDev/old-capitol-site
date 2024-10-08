package com.econnect.barangaymanagementapp.util.state;

import com.econnect.barangaymanagementapp.enumeration.type.NavigationType;

public class NavigationState {
    private NavigationType activeItem;

    public NavigationType getActiveItem() {
        return activeItem;
    }

    public void setActiveItem(NavigationType activeItem) {
        this.activeItem = activeItem;
    }
}