package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Borrow;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.BorrowStatus;
import com.econnect.barangaymanagementapp.repository.BorrowRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.BorrowStatus.PENDING;

public class BorrowService {
    private final BorrowRepository borrowRepository;

    public BorrowService(DependencyInjector dependencyInjector) {
        this.borrowRepository = dependencyInjector.getBorrowRepository();
    }

    public Response createBorrow(Borrow borrow) {
        int baseId = 1000;
        String residentId = borrow.getId();
        int countOfBorrows = findCountOfBorrowsByResidentId(residentId);
        int autoIncrementId = countOfBorrows > 0 ? baseId + countOfBorrows : baseId;
        borrow.setId(borrow.getId() + "-" + autoIncrementId);
        borrow.setCreatedAt(ZonedDateTime.now());
        borrow.setUpdatedAt(ZonedDateTime.now());
        borrow.setApplicationType(ApplicationType.WALK_IN);
        borrow.setStatus(BorrowStatus.PENDING);
        return borrowRepository.createBorrow(borrow);
    }

    public List<Borrow> findAllBorrows() {
        return borrowRepository.findAllBorrows();
    }

    public List<Borrow> findAllPendingBorrows() {
        return borrowRepository.findBorrowByFilter(request -> request.getStatus().equals(PENDING));
    }

    public Optional<Borrow> findBorrowById(String id) {
        return borrowRepository.findBorrowById(id);
    }

    private int findCountOfBorrowsByResidentId(String residentId) {
        return (int) borrowRepository.findBorrowByFilter(request -> request.getId().contains(residentId)).stream().count();
    }

    public Optional<Borrow> findCompletedBorrow(String id) {
        return borrowRepository.findBorrowById(id).filter(request -> request.getStatus().equals(BorrowStatus.RETURNED));
    }

    public Response updateBorrowByStatus(String requestId, BorrowStatus status) {
        return borrowRepository.updateBorrowByStatus(requestId, status);
    }

    private String generateReferenceNumber() {
        int OTP_LENGTH = 12;
        SecureRandom random = new SecureRandom();
        long otp = random.nextLong((long) Math.pow(10, OTP_LENGTH));
        return String.format("%012d", otp);
    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        borrowRepository.enableLiveReload(handleDataUpdate);
    }
}
