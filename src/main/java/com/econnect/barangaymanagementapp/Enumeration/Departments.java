package com.econnect.barangaymanagementapp.Enumeration;

import java.util.List;

import static com.econnect.barangaymanagementapp.Enumeration.NavigationItems.*;

public enum Departments {
    HUMAN_RESOURCES("Human Resources", "View/HumanResources/Dashboard.fxml", List.of(DASHBOARD, ANALYTICS, EMPLOYEES)),
    BARANGAY_OFFICE("Barangay Office", "View/BarangayOffice/Dashboard.fxml", List.of(DASHBOARD, ANALYTICS, RESIDENTS, HISTORY)),
    HEALTH_OFFICE("Health Office", "View/HealthOffice/Dashboard.fxml", List.of(DASHBOARD, ANALYTICS)),;

    private final String name;
    private final String link;
    private final List<NavigationItems> navigationItemsList;
    private NavigationItems activeItem;

    Departments(String name, String link, List<NavigationItems> navigationItemsList) {
        this.name = name;
        this.link = link;
        this.navigationItemsList = navigationItemsList;
    }

    public String getName() {
        return name;
    }

    public String getDirectoryName() {
        return name.replace(" ", "");
    }

    public String getLink() {
        return link;
    }

    public List<NavigationItems> getNavigationItems() {
        return navigationItemsList;
    }

    public NavigationItems getCurrentActiveItem() {
        return activeItem;
    }

    public void setCurrentActiveItem(NavigationItems activeItem) {
        this.activeItem = activeItem;
    }
}
