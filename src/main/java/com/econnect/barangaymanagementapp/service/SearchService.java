package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.domain.Request;
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
                || DateFormatter.formatDateToLongStyle(employee.getCreatedAt()).toLowerCase().contains(searchText)
                || DateFormatter.formatTimeTo12HourStyle(employee.getCreatedAt()).toLowerCase().contains(searchText);
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
        return resident -> {
            return (resident.getId() != null && resident.getId().toLowerCase().contains(searchText))
                    || (resident.getFirstName() != null && resident.getFirstName().toLowerCase().contains(searchText))
                    || (resident.getMiddleName() != null && resident.getMiddleName().toLowerCase().contains(searchText))
                    || (resident.getLastName() != null && resident.getLastName().toLowerCase().contains(searchText))
                    || (resident.getNameExtension() != null && resident.getNameExtension().toLowerCase().contains(searchText))
                    || (resident.getMobileNumber() != null && resident.getMobileNumber().toLowerCase().contains(searchText))
                    || (resident.getEmail() != null && resident.getEmail().toLowerCase().contains(searchText))
                    || (resident.getAddress() != null && resident.getAddress().toLowerCase().contains(searchText))
                    || (resident.getSex() != null && resident.getSex().getName().toLowerCase().contains(searchText))
                    || (resident.getBirthdate() != null && resident.getBirthdate().toLowerCase().contains(searchText))
                    || (resident.getBirthdate() != null && DateFormatter.calculateAgeFromBirthdate(resident.getBirthdate()).contains(searchText))
                    || (resident.getBirthplace() != null && resident.getBirthplace().toLowerCase().contains(searchText))
                    || (resident.getCitizenship() != null && resident.getCitizenship().toLowerCase().contains(searchText))
                    || (resident.getOccupation() != null && resident.getOccupation().toLowerCase().contains(searchText))
                    || (resident.getCivilStatus() != null && resident.getCivilStatus().getName() != null && resident.getCivilStatus().getName().toLowerCase().contains(searchText))
                    || (resident.getMotherTounge() != null && resident.getMotherTounge().toLowerCase().contains(searchText))
                    || (resident.getBloodType() != null && resident.getBloodType().getName() != null && resident.getBloodType().getName().toLowerCase().contains(searchText))
                    || (resident.getReligion() != null && resident.getReligion().toLowerCase().contains(searchText))
                    || (resident.getFatherFirstName() != null && resident.getFatherFirstName().toLowerCase().contains(searchText))
                    || (resident.getFatherLastName() != null && resident.getFatherLastName().toLowerCase().contains(searchText))
                    || (resident.getFatherMiddleName() != null && resident.getFatherMiddleName().toLowerCase().contains(searchText))
                    || (resident.getFatherSuffixName() != null && resident.getFatherSuffixName().toLowerCase().contains(searchText))
                    || (resident.getFatherOccupation() != null && resident.getFatherOccupation().toLowerCase().contains(searchText))
                    || (resident.getMotherFirstName() != null && resident.getMotherFirstName().toLowerCase().contains(searchText))
                    || (resident.getMotherLastName() != null && resident.getMotherLastName().toLowerCase().contains(searchText))
                    || (resident.getMotherMiddleName() != null && resident.getMotherMiddleName().toLowerCase().contains(searchText))
                    || (resident.getMotherSuffixName() != null && resident.getMotherSuffixName().toLowerCase().contains(searchText))
                    || (resident.getMotherOccupation() != null && resident.getMotherOccupation().toLowerCase().contains(searchText))
                    || (resident.getSpouseFirstName() != null && resident.getSpouseFirstName().toLowerCase().contains(searchText))
                    || (resident.getSpouseLastName() != null && resident.getSpouseLastName().toLowerCase().contains(searchText))
                    || (resident.getSpouseMiddleName() != null && resident.getSpouseMiddleName().toLowerCase().contains(searchText))
                    || (resident.getSpouseSuffixName() != null && resident.getSpouseSuffixName().toLowerCase().contains(searchText))
                    || (resident.getSpouseOccupation() != null && resident.getSpouseOccupation().toLowerCase().contains(searchText))
                    || (resident.getResidencyStatus() != null && resident.getResidencyStatus().getName().toLowerCase().contains(searchText))
                    || (resident.getStatus() != null && resident.getStatus().getName() != null && resident.getStatus().getName().toLowerCase().contains(searchText))
                    || (resident.getEducationalAttainment() != null && resident.getEducationalAttainment().toLowerCase().contains(searchText))
                    || (resident.getSourceOfIncome() != null && resident.getSourceOfIncome().toLowerCase().contains(searchText))
                    || (resident.getCreatedAt() != null && DateFormatter.formatDateToLongStyle(resident.getCreatedAt()).toLowerCase().contains(searchText))
                    || (resident.getCreatedAt() != null && DateFormatter.formatTimeTo12HourStyle(resident.getCreatedAt()).toLowerCase().contains(searchText))
                    || (resident.getUpdatedAt() != null && DateFormatter.formatDateToLongStyle(resident.getUpdatedAt()).toLowerCase().contains(searchText))
                    || (resident.getUpdatedAt() != null && DateFormatter.formatTimeTo12HourStyle(resident.getUpdatedAt()).toLowerCase().contains(searchText));

        };
    }

    public Predicate<Request> createRequestFilter(String searchText) {
        return request -> request.getId().toLowerCase().contains(searchText)
                || request.getReferenceNumber().toLowerCase().contains(searchText)
                || request.getApplicationType().getName().toLowerCase().contains(searchText)
                || request.getRequestType().getName().toLowerCase().contains(searchText)
                || request.getStatus().getName().toLowerCase().contains(searchText)
                || request.getRequest().toLowerCase().contains(searchText)
                || request.getCreatedAt() != null && DateFormatter.formatDateToLongStyle(request.getCreatedAt()).toLowerCase().contains(searchText)
                || request.getCreatedAt() != null && DateFormatter.formatTimeTo12HourStyle(request.getCreatedAt()).toLowerCase().contains(searchText)
                || request.getCreatedAt() != null && DateFormatter.formatToDateTime(request.getUpdatedAt()).toLowerCase().contains(searchText);
    }

    public Predicate<Inventory> createInventoryFilter(String searchText) {
        return inventory -> inventory.getId().toLowerCase().contains(searchText)
                || inventory.getItemName().toLowerCase().contains(searchText)
                || inventory.getItemType().toLowerCase().contains(searchText)
                || inventory.getStocks().toLowerCase().contains(searchText);
    }
}
