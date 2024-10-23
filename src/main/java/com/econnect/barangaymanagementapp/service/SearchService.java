package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Resident;
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

    public Predicate<Resident> createResidentFilter(String searchText) {
        return resident -> resident.getId().toLowerCase().contains(searchText)
                || resident.getFirstName().toLowerCase().contains(searchText)
                || resident.getMiddleName().toLowerCase().contains(searchText)
                || resident.getLastName().toLowerCase().contains(searchText)
                || resident.getNameExtension().toLowerCase().contains(searchText)
                || resident.getContactNumber().toLowerCase().contains(searchText)
                || resident.getEmail().toLowerCase().contains(searchText)
                || resident.getAddress().toLowerCase().contains(searchText)
                || resident.getSex().getName().toLowerCase().contains(searchText)
                || resident.getBirthdate().toLowerCase().contains(searchText)
                || resident.getAge().toLowerCase().contains(searchText)
                || resident.getBirthplace().toLowerCase().contains(searchText)
                || resident.getCitizenship().toLowerCase().contains(searchText)
                || resident.getOccupation().toLowerCase().contains(searchText)
                || resident.getCivilStatus().getName().toLowerCase().contains(searchText)
                || resident.getMotherTounge().getName().toLowerCase().contains(searchText)
                || resident.getBloodType().getName().toLowerCase().contains(searchText)
                || resident.getReligion().getName().toLowerCase().contains(searchText)
                || resident.getFatherFirstName().toLowerCase().contains(searchText)
                || resident.getFatherLastName().toLowerCase().contains(searchText)
                || resident.getFatherMiddleName().toLowerCase().contains(searchText)
                || resident.getFatherSuffixName().toLowerCase().contains(searchText)
                || resident.getFatherOccupation().toLowerCase().contains(searchText)
                || resident.getFatherBirthdate().toLowerCase().contains(searchText)
                || resident.getMotherFirstName().toLowerCase().contains(searchText)
                || resident.getMotherLastName().toLowerCase().contains(searchText)
                || resident.getMotherMiddleName().toLowerCase().contains(searchText)
                || resident.getMotherSuffixName().toLowerCase().contains(searchText)
                || resident.getMotherOccupation().toLowerCase().contains(searchText)
                || resident.getMotherBirthdate().toLowerCase().contains(searchText)
                || resident.getSpouseFirstName().toLowerCase().contains(searchText)
                || resident.getSpouseLastName().toLowerCase().contains(searchText)
                || resident.getSpouseMiddleName().toLowerCase().contains(searchText)
                || resident.getSpouseSuffixName().toLowerCase().contains(searchText)
                || resident.getSpouseOccupation().toLowerCase().contains(searchText)
                || resident.getSpouseBirthdate().toLowerCase().contains(searchText)
                || resident.getHouseHoldIncome().toLowerCase().contains(searchText)
                || resident.getEconomicLevel().getName().toLowerCase().contains(searchText)
                || resident.getStatus().getName().toLowerCase().contains(searchText)
                || resident.getProfileUrl().toLowerCase().contains(searchText)
                || resident.getValidIdURL().toLowerCase().contains(searchText)
                || DateFormatter.extractDateAndFormat(resident.getCreatedAt()).toLowerCase().contains(searchText)
                || DateFormatter.extractTimeAndFormat(resident.getCreatedAt()).toLowerCase().contains(searchText)
                || DateFormatter.extractDateAndFormat(resident.getUpdatedAt()).toLowerCase().contains(searchText)
                || DateFormatter.extractTimeAndFormat(resident.getUpdatedAt()).toLowerCase().contains(searchText);
    }
}
