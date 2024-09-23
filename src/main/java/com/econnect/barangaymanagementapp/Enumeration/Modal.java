package com.econnect.barangaymanagementapp.Enumeration;

public enum Modal {
    DEFAULT("View/Component/Modal/default.fxml", Sound.DEFAULT),
    CONFIRM("View/Component/Modal/confirm.fxml", Sound.DEFAULT),
    APPROVE("View/Component/Modal/approve.fxml", Sound.DEFAULT),
    REJECT("View/Component/Modal/reject.fxml", Sound.DEFAULT),
    ERROR("View/Component/Modal/error.fxml", Sound.ERROR),
    SUCCESS("View/Component/Modal/success.fxml", Sound.SUCCESS);

    private final String link;
    private final Sound sound;

    Modal(String link, Sound sound) {
        this.link = link;
        this.sound = sound;
    }

    public String getFXMLPath() {
        return link;
    }

    public Sound getSound() {
        return sound;
    }
}
