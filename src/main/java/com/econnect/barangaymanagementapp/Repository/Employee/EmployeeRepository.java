package com.econnect.barangaymanagementapp.Repository.Employee;

import com.econnect.barangaymanagementapp.Config.Config;
import com.econnect.barangaymanagementapp.Database.InMemoryDatabase;
import com.econnect.barangaymanagementapp.Domain.Employee;
import com.econnect.barangaymanagementapp.Enumeration.ApiPath;
import com.econnect.barangaymanagementapp.Utils.DependencyInjector;
import com.econnect.barangaymanagementapp.Utils.HTTPClient;
import com.econnect.barangaymanagementapp.Utils.JsonConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class EmployeeRepository implements IEmployeeRepository {
    private final String apiKey = Config.getFirebaseUrl() + ApiPath.EMPLOYEES.getPath();
    private final HTTPClient client;
    private final JsonConverter jsonConverter;

    public EmployeeRepository(DependencyInjector dependencyInjector) {
        this.client = dependencyInjector.getHttpClient();
        this.jsonConverter = dependencyInjector.getJsonConverter();
    }

    @Override
    public Response createEmployee(Employee employee) {
        Request request = new Request.Builder()
                .url(apiKey + "/" + employee.getId() + ".json")
                .put(RequestBody.create(
                        jsonConverter.convertObjectToJson(employee), MediaType.parse("application/json")
                ))
                .build();

        try (Response response = client.getClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error, code: " + response.code() + " - " + response.message());
            }
            return response;
        } catch (IOException e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        return null;
    }

    @Override
    public void deleteEmployee(Employee employee) {

    }

    @Override
    public Employee findEmployeeById(int id) {
        return null;
    }

    @Override
    public List<Employee> findAllEmployees() {
        Request request = new Request.Builder()
                .url(apiKey + ".json")
                .get()
                .build();

        try (Response response = client.getClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error, code: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();

            if (responseBody.equals("\"\"") || responseBody.isEmpty() || responseBody.equals("null")) {
                return Collections.emptyList();
            }

            Map<String, Employee> employeesMap = jsonConverter.convertJsonToObject(responseBody, new TypeReference<>() {
            });

            employeesMap.forEach((id, employee) -> {
                employee.setId(id);
            });

            return new ArrayList<>(employeesMap.values());
        } catch (IOException e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<Employee> findEmployeeByCredentials(String username, String password) {
        try {
            ArrayList<Employee> employees = InMemoryDatabase.getInstance().getList();
            return employees.stream()
                    .filter(employee -> employee.getUsername().equals(username) && employee.getAccess().equals(password))
                    .findFirst();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
