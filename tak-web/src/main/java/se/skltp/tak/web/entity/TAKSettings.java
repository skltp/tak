
package se.skltp.tak.web.entity;

import jakarta.validation.constraints.NotBlank;

import jakarta.persistence.*;

@Entity
public class TAKSettings {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Version
    private long version;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String settingName;

    @NotBlank
    @Column(nullable = false, columnDefinition = "text")
    private String settingValue;

    @Override
    public String toString() {
        return settingName + " = " + settingValue;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }

    public String getSettingName() {
        return settingName;
    }
    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public String getSettingValue() {
        return settingValue;
    }
    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
}
