package com.econnect.barangaymanagementapp.service;

import com.econnect.barangaymanagementapp.domain.Borrow;
import com.econnect.barangaymanagementapp.domain.Cedula;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.enumeration.type.ApplicationType;
import com.econnect.barangaymanagementapp.enumeration.type.StatusType.BorrowStatus;
import com.econnect.barangaymanagementapp.repository.BorrowRepository;
import com.econnect.barangaymanagementapp.util.DependencyInjector;
import okhttp3.Request;
import okhttp3.Response;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.BorrowStatus.PENDING;

public class BorrowService {
    private final BorrowRepository borrowRepository;
    private final InventoryService inventoryService;
    private final EmailService emailService;
    private final ResidentService residentService;

    public BorrowService(DependencyInjector dependencyInjector) {
        this.borrowRepository = dependencyInjector.getBorrowRepository();
        this.inventoryService = dependencyInjector.getInventoryService();
        this.emailService = dependencyInjector.getEmailService();
        this.residentService = dependencyInjector.getResidentService();
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

    public Response updateBorrowByStatus(String requestId, String itemId, BorrowStatus status) {
        Optional<Borrow> getBorrow = this.findBorrowById(requestId);

        if (status.equals(BorrowStatus.IN_PROGRESS)) {
            Borrow borrow = getBorrow.orElseThrow(() -> new RuntimeException("Borrow not found"));

            Optional<Inventory> item = inventoryService.findInventoryById(itemId);
            return item.map(inventory -> {
                int newStock = Integer.parseInt(inventory.getStocks()) - Integer.parseInt(borrow.getQuantity());
                if (newStock < 0) {
                    throw new IllegalArgumentException("Insufficient stocks for item");
                }
                inventory.setStocks(String.valueOf(newStock));
                inventoryService.updateInventory(inventory);
                return borrowRepository.updateBorrowByStatus(requestId, status);
            }).orElseGet(() -> {
                throw new RuntimeException("Item not found");
            });
        }

        if (status.equals(BorrowStatus.RETURNED) || status.equals(BorrowStatus.CANCELLED)) {
            Borrow borrow = getBorrow.orElseThrow(() -> new RuntimeException("Borrow not found"));

            Optional<Inventory> item = inventoryService.findInventoryById(itemId);
            item.ifPresentOrElse(inventory -> {
                int newStock = Integer.parseInt(inventory.getStocks()) + Integer.parseInt(borrow.getQuantity());
                inventory.setStocks(String.valueOf(newStock));
                inventoryService.updateInventory(inventory);
            }, () -> {
                throw new RuntimeException("Item not found");
            });
        }

        if (status.equals(BorrowStatus.RELEASING) && getBorrow.get().getApplicationType().equals(ApplicationType.ONLINE)) {
            Resident resident = residentService.findResidentById(requestId.substring(0, 12)).orElseThrow();
            sendReleaseEmail(resident, getBorrow.get());
        }

        return borrowRepository.updateBorrowByStatus(requestId, status);
    }

    //Analytics
    public int totalBorrows() {
        return borrowRepository.findAllBorrows().size();
    }

    public int totalBorrowRequests() {
        return borrowRepository.findBorrowByFilter(request -> !request.getStatus().equals(BorrowStatus.RETURNED)).size();
    }

    public int totalPendingBorrows() {
        return borrowRepository.findBorrowByFilter(request -> request.getStatus().equals(PENDING)).size();
    }

    public int todayTotalBorrowRequests() {
        return borrowRepository.findBorrowByFilter(request -> request.getCreatedAt().toLocalDate().equals(ZonedDateTime.now().toLocalDate())).size();
    }

    public int totalCompletedBorrowRequests() {
        return borrowRepository.findBorrowByFilter(request -> request.getStatus().equals(BorrowStatus.RETURNED)).size();
    }

    public void listenToUpdates(Consumer<String> handleDataUpdate) {
        borrowRepository.enableLiveReload(handleDataUpdate);
    }

    private void sendReleaseEmail(Resident resident, Borrow borrow) {
        emailService.sendEmailAsync(resident.getEmail(), "Your Request Item is Ready for Pickup", String.format("""
                        Dear %s,

                        We are pleased to inform you that your borrowed %s has been processed and is now ready for pickup.
                                                    
                        Please visit the barangay hall at Old Capitol Site to collect your borrowed item. Our office hours are from 8:00 AM to 5:00 PM, Monday to Friday.
                                                    
                        If you have any questions or need further assistance, feel free to reach out to our office.
                                                        
                        Best regards,
                        Old Capitol Site
                        """,
                resident.getFirstName(),
                borrow.getItemName()
        ));
    }
}
