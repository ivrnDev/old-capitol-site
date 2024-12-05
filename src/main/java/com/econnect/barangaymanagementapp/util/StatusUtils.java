package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.enumeration.type.StatusType;

import java.util.Set;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.*;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.EmployeeStatus;
import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus;

public class StatusUtils {
    public static final Set<ResidentStatus> INACTIVE_RESIDENT = Set.of(ResidentStatus.REMOVED, ResidentStatus.PENDING, ResidentStatus.REJECTED);

    public static final Set<EmployeeStatus> APPLICANTS_STATUSES = Set.of(EmployeeStatus.PENDING, EmployeeStatus.UNDER_REVIEW, EmployeeStatus.EVALUATION);
    public static final Set<EmployeeStatus> PROCESSING_APPLICANTS = Set.of(EmployeeStatus.UNDER_REVIEW, EmployeeStatus.EVALUATION);
    public static final Set<EmployeeStatus> TODAY_APPLICANTS_EMPLOYEES = Set.of(EmployeeStatus.UNDER_REVIEW, EmployeeStatus.PENDING);

    public static final Set<CertificateStatus> TOTAL_PROCESSING_CERTIFICATES = Set.of(CertificateStatus.IN_PROGRESS, CertificateStatus.RELEASING);
    public static final Set<EventAppointmentStatus> PROCESSING_EVENTS = Set.of(EventAppointmentStatus.IN_PROGRESS, EventAppointmentStatus.WAITING);

}
