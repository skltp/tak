/**
 * Copyright (c) 2013 Center för eHälsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
