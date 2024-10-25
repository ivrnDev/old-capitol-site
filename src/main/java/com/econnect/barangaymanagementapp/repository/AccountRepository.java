package com.econnect.barangaymanagementapp.repository;

import com.econnect.barangaymanagementapp.config.Config;
import com.econnect.barangaymanagementapp.domain.Account;
import com.econnect.barangaymanagementapp.enumeration.database.Firebase;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.util.Optional;

public class AccountRepository extends BaseRepository<Account> {
    private final String apiKey = Config.getFirebaseUrl() + Firebase.ACCOUNTS.getPath();

    public AccountRepository(DependencyInjector dependencyInjector) {
        super(dependencyInjector);
    }

    public Optional<Account> findAccountById(String id) {
        return findById(apiKey, id, new TypeReference<>() {
        });
    }

    public Response updateAccountByStatus(String id, StatusType.ResidentStatus status) {
        return updateBy(apiKey, id, new TypeReference<>() {
        }, account -> account.setStatus(status));
    }
}
