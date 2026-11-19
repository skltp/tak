/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
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
    @Lob
    @Column(nullable = false)
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
