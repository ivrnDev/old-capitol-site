package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.enumeration.type.StatusType;

import java.util.Set;

import static com.econnect.barangaymanagementapp.enumeration.type.StatusType.ResidentStatus.*;

public class StatusUtils {
    public static final Set<StatusType.ResidentStatus> INACTIVE_RESIDENT = Set.of(REMOVED, PENDING, REJECTED);

}
