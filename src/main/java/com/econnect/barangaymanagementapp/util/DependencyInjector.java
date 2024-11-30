package com.econnect.barangaymanagementapp.util;

import com.econnect.barangaymanagementapp.domain.Employee;
import com.econnect.barangaymanagementapp.domain.Inventory;
import com.econnect.barangaymanagementapp.domain.Request;
import com.econnect.barangaymanagementapp.domain.Resident;
import com.econnect.barangaymanagementapp.repository.*;
import com.econnect.barangaymanagementapp.service.*;
import com.econnect.barangaymanagementapp.util.data.JsonConverter;
import com.econnect.barangaymanagementapp.util.resource.SoundUtils;
import com.econnect.barangaymanagementapp.util.state.NavigationState;
import com.econnect.barangaymanagementapp.util.state.UserSession;
import com.econnect.barangaymanagementapp.util.ui.ButtonUtils;
import com.econnect.barangaymanagementapp.util.ui.FileChooserUtils;
import com.econnect.barangaymanagementapp.util.ui.ModalUtils;
import javafx.stage.Stage;
import lombok.Getter;


@Getter
public class DependencyInjector {
    private final Stage stage;
    private final SceneManager sceneManager;
    private final UserSession userSession;
    private final NavigationState navigationState;
    private final SoundUtils soundUtils;
    private final FXMLLoaderFactory fxmlLoaderFactory;
    private final ModalUtils modalUtils;
    private final ButtonUtils buttonUtils;
    private final JsonConverter jsonConverter;
    private final HTTPClient httpClient;
    private final ImageService imageService;
    private final FileChooserUtils fileChooserUtils;
    private final SearchService<Employee> employeeSearchService;
    private final SearchService<Resident> residentSearchService;
    private final SearchService<Request> requestSearchService;
    private final SearchService<Inventory> inventorySearchService;
    private final Validator validator;
    private final LiveReloadUtils liveReloadUtils;
    private final WebCam webCam;
    private final UploadImageUtils uploadImageUtils;
    private final PrintUtils printUtils;

    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final ResidentRepository residentRepository;
    private final CertificateRepository certificateRepository;
    private final BarangayIdRepository barangayIdRepository;
    private final CedulaRepository cedulaRepository;
    private final ComplaintRepository complaintRepository;
    private final PatientRepository patientRepository;
    private final HealthAppointmentRepository healthAppointmentRepository;
    private final InventoryRepository inventoryRepository;
    private final BorrowRepository borrowRepository;

    private final EmailService emailService;
    private final LoginService loginService;
    private final EmployeeService employeeService;
    private final ResidentService residentService;
    private final CertificateService certificateService;
    private final BarangayidService barangayidService;
    private final CedulaService cedulaService;
    private final ComplaintService complaintService;
    private final PatientService patientService;
    private final HealthAppointmentService healthAppointmentService;
    private final InventoryService inventoryService;
    private final BorrowService borrowService;

    public DependencyInjector(Stage stage) {
        this.stage = stage;
        this.soundUtils = new SoundUtils();
        this.navigationState = new NavigationState();
        this.fxmlLoaderFactory = new FXMLLoaderFactory(this);
        this.userSession = UserSession.getInstance();
        this.sceneManager = new SceneManager(this);
        this.modalUtils = new ModalUtils(this);
        this.buttonUtils = new ButtonUtils();
        this.jsonConverter = new JsonConverter();
        this.httpClient = new HTTPClient();
        this.imageService = new ImageService(this);
        this.fileChooserUtils = new FileChooserUtils();
        this.validator = new Validator(this);
        this.emailService = new EmailService();
        this.liveReloadUtils = new LiveReloadUtils();
        this.webCam = new WebCam();
        this.uploadImageUtils = new UploadImageUtils(this);
        this.printUtils = new PrintUtils();

        this.accountRepository = new AccountRepository(this);
        this.employeeRepository = new EmployeeRepository(this);
        this.residentRepository = new ResidentRepository(this);
        this.certificateRepository = new CertificateRepository(this);
        this.barangayIdRepository = new BarangayIdRepository(this);
        this.cedulaRepository = new CedulaRepository(this);
        this.complaintRepository = new ComplaintRepository(this);
        this.patientRepository = new PatientRepository(this);
        this.healthAppointmentRepository = new HealthAppointmentRepository(this);
        this.inventoryRepository = new InventoryRepository(this);
        this.borrowRepository = new BorrowRepository(this);

        this.employeeService = new EmployeeService(this);
        this.residentService = new ResidentService(this);
        this.loginService = new LoginService(this);
        this.certificateService = new CertificateService(this);
        this.employeeSearchService = new SearchService<>();
        this.residentSearchService = new SearchService<>();
        this.requestSearchService = new SearchService<>();
        this.inventorySearchService = new SearchService<>();
        this.barangayidService = new BarangayidService(this);
        this.cedulaService = new CedulaService(this);
        this.complaintService = new ComplaintService(this);
        this.patientService = new PatientService(this);
        this.healthAppointmentService = new HealthAppointmentService(this);
        this.inventoryService = new InventoryService(this);
        this.borrowService = new BorrowService(this);
    }
}
