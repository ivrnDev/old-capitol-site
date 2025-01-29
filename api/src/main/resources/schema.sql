CREATE DATABASE IF NOT EXISTS BMS;
USE BMS;

CREATE TABLE Employees
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstName              VARCHAR(255)                                                                                   NOT NULL,
    lastName               VARCHAR(255)                                                                                   NOT NULL,
    middleName             VARCHAR(255),
    email                  VARCHAR(255)                                                                                   NOT NULL UNIQUE,
    contactNumber          VARCHAR(15)                                                                                    NOT NULL,
    address                VARCHAR(255)                                                                                   NOT NULL,
    role                   ENUM (
        -- Administrative Department
        'ADMIN',
        'HR_MANAGER',
        'ADMINISTRATIVE_CLERK',
        'OFFICE_FRONT_DESK',
        'EVENT_COORDINATOR',
        'DOCUMENT_CLERK',
        'SECRETARY',

        -- Web Department
        'WEB_ADMINISTRATOR',

        -- Health Department
        'GENERAL_DOCTOR',
        'DENTAL',
        'MIDWIFE',
        'DNS',
        'BHW',
        'HEALTH_COMMITTEE_HEAD',
        -- Lupon Department
        'LUPON_MEMBERS'
        )                                                                                                                 NOT NULL,
    status                 ENUM ('PENDING', 'UNDER_REVIEW', 'EVALUATION', 'ACTIVE', 'RESIGNED', 'REJECTED', 'TERMINATED') NOT NULL,
    department             ENUM ('OFFICE', 'HEALTH', 'LUPON', 'WEB_ADMIN')                                                NOT NULL,
    employment             ENUM ('FULL_TIME', 'VOLUNTEER')                                                                NOT NULL,
    username               VARCHAR(255)                                                                                   NOT NULL UNIQUE,
    password               VARCHAR(255)                                                                                   NOT NULL,
    profileUrl             VARCHAR(255)                                                                                   NOT NULL,
    resumeUrl              VARCHAR(255)                                                                                   NOT NULL,
    nbiClearanceUrl        VARCHAR(255)                                                                                   NOT NULL,
    nbiClearanceExpiration DATE                                                                                           NOT NULL,
    applicationType        ENUM ('WALK_IN', 'ONLINE')                                                                     NOT NULL,
    createdAt              DATETIME                                                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt              DATETIME                                                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE Residents
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstName             VARCHAR(255)                                         NOT NULL,
    middleName            VARCHAR(255),
    lastName              VARCHAR(255)                                         NOT NULL,
    suffixName            VARCHAR(50),
    mobileNumber          VARCHAR(15)                                          NOT NULL,
    telephoneNumber       VARCHAR(15),
    email                 VARCHAR(255)                                         NOT NULL UNIQUE,
    address               VARCHAR(255)                                         NOT NULL,
    sex                   ENUM ('MALE', 'FEMALE')                              NOT NULL,
    birthdate             DATE                                                 NOT NULL,
    birthplace            VARCHAR(255)                                         NOT NULL,
    citizenship           VARCHAR(100)                                         NOT NULL,
    occupation            VARCHAR(255),
    civilStatus           ENUM ('SINGLE', 'MARRIED')                           NOT NULL,
    motherTounge          VARCHAR(100),
    bloodType             ENUM (
        'A+',
        'A-',
        'B+',
        'B-',
        'AB+',
        'AB-',
        'O+',
        'O-',
        'UNKNOWN'
        )                                                                      NOT NULL,
    religion              VARCHAR(100),

    fatherFirstName       VARCHAR(255)                                         NOT NULL,
    fatherLastName        VARCHAR(255)                                         NOT NULL,
    fatherMiddleName      VARCHAR(255),
    fatherSuffixName      ENUM ('NONE', 'JR', 'SR', 'I', 'II', 'III', 'IV', 'V'),
    fatherOccupation      VARCHAR(255),

    motherFirstName       VARCHAR(255)                                         NOT NULL,
    motherLastName        VARCHAR(255)                                         NOT NULL,
    motherMiddleName      VARCHAR(255),
    motherSuffixName      ENUM ('NONE', 'JR', 'SR', 'I', 'II', 'III', 'IV', 'V'),
    motherOccupation      VARCHAR(255),

    spouseFirstName       VARCHAR(255),
    spouseLastName        VARCHAR(255),
    spouseMiddleName      VARCHAR(255),
    spouseSuffixName      ENUM ('NONE', 'JR', 'SR', 'I', 'II', 'III', 'IV', 'V'),
    spouseOccupation      VARCHAR(255),

    emergencyFirstName    VARCHAR(255)                                         NOT NULL,
    emergencyLastName     VARCHAR(255)                                         NOT NULL,
    emergencyMiddleName   VARCHAR(255),
    emergencyMobileNumber VARCHAR(15)                                          NOT NULL,
    emergencyRelationship VARCHAR(100)                                         NOT NULL,

    sourceOfIncome        VARCHAR(255),
    residencyStatus       ENUM ('RENTER', 'OWNER', 'SHARER')                   NOT NULL,
    status                ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DECEASED') NOT NULL,
    educationalAttainment VARCHAR(255),
    profileUrl            VARCHAR(255),
    validIdUrl            VARCHAR(255),
    validIdExpiration     DATE,
    tinIdUrl              VARCHAR(255),
    tinIdNumber           VARCHAR(50),

    createdAt             DATETIME                                             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt             DATETIME                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_employees_email ON Employees (email);
CREATE INDEX idx_residents_email ON Residents (email);
