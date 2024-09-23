package com.econnect.barangaymanagementapp.Enumeration;

import static com.econnect.barangaymanagementapp.Enumeration.ModalType.*;

public enum Modal {
    CLASSIC("View/Component/Modal/classic.fxml", Sound.DEFAULT, MODAL),
    DEFAULT("View/Component/Modal/default.fxml", Sound.DEFAULT, MODAL),
    DEFAULT_APPROVE("View/Component/Modal/default.fxml", Sound.DEFAULT, MODAL),
    DEFAULT_REJECT("View/Component/Modal/default.fxml", Sound.DEFAULT, MODAL),
    SUCCESS("View/Component/Modal/notification.fxml", Sound.SUCCESS, NOTIFICATION),
    WARNING("View/Component/Modal/notification.fxml", Sound.WARNING, NOTIFICATION),
    ERROR("View/Component/Modal/notification.fxml", Sound.ERROR, NOTIFICATION),
    ;

    private final String link;
    private final Sound sound;
    private final ModalType modalType;


    Modal(String link, Sound sound, ModalType type) {
        this.link = link;
        this.sound = sound;
        this.modalType = type;

    }

    public String getFXMLPath() {
        return link;
    }

    public Sound getSound() {
        return sound;
    }

    public ModalType getModalType() {
        return modalType;
    }

}

