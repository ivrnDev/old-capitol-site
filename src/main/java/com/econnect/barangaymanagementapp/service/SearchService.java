package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.util.DateFormatter;
import javafx.concurrent.Task;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchService<T> {

    public List<T> search(List<T> items, String searchText, Predicate<T> filterCondition) {
        return items.stream()
                .filter(filterCondition)
                .collect(Collectors.toList());
    }

    public void performSearch(
            String searchText,
            List<T> allItems,
            Predicate<T> filterCondition,
            Consumer<List<T>> callback) {

        Task<List<T>> searchTask = new Task<>() {
            @Override
            protected List<T> call() {
                return search(allItems, searchText, filterCondition);
            }

            @Override
            protected void succeeded() {
                List<T> filteredItems = getValue();
                callback.accept(filteredItems);
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                System.err.println("Error filtering items: " + exception.getMessage());
            }
        };

        new Thread(searchTask).start();
    }

    //Filter conditions
    public Predicate<Employee> createEmployeeApplicationFilter(String searchText) {
        return employee -> employee.getId().toLowerCase().contains(searchText)
                || employee.getFirstName().toLowerCase().contains(searchText)
                || employee.getLastName().toLowerCase().contains(searchText)
                || employee.getStatus().getName().toLowerCase().contains(searchText)
                || employee.getApplicationType().getName().toLowerCase().contains(searchText)
                || employee.getCreatedAt().toString().contains(searchText)
                || DateFormatter.extractDateAndFormat(employee.getCreatedAt()).toLowerCase().contains(searchText)
                || DateFormatter.extractTimeAndFormat(employee.getCreatedAt()).toLowerCase().contains(searchText);
    }

    public Predicate<Employee> createEmployeeFilter(String searchText) {
        return employee -> employee.getId().toLowerCase().contains(searchText)
                || employee.getFirstName().toLowerCase().contains(searchText)
                || employee.getLastName().toLowerCase().contains(searchText)
                || employee.getRole().getName().toLowerCase().contains(searchText)
                || employee.getStatus().getName().toLowerCase().contains(searchText)
                || employee.getDepartment().getName().toLowerCase().contains(searchText);
    }
}
