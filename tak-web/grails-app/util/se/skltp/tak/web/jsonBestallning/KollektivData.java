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
package se.skltp.tak.web.jsonBestallning;

import java.util.List;

public class KollektivData {
    private List<TjanstekontraktBestallning> tjanstekontrakt = null;
    private List<LogiskadressBestallning> logiskadress = null;
    private List<TjanstekomponentBestallning> tjanstekomponent = null;
    private List<AnropsbehorighetBestallning> anropsbehorighet = null;
    private List<VagvalBestallning> vagval = null;

    public List<TjanstekontraktBestallning> getTjanstekontrakt() {
        return tjanstekontrakt;
    }

    public void setTjanstekontrakt(List<TjanstekontraktBestallning> tjanstekontrakt) {
        this.tjanstekontrakt = tjanstekontrakt;
    }

    public List<LogiskadressBestallning> getLogiskadress() {
        return logiskadress;
    }

    public void setLogiskadress(List<LogiskadressBestallning> logiskadress) {
        this.logiskadress = logiskadress;
    }

    public List<TjanstekomponentBestallning> getTjanstekomponent() {
        return tjanstekomponent;
    }

    public void setTjanstekomponent(List<TjanstekomponentBestallning> tjanstekomponent) {
        this.tjanstekomponent = tjanstekomponent;
    }

    public List<AnropsbehorighetBestallning> getAnropsbehorighet() {
        return anropsbehorighet;
    }

    public void setAnropsbehorighet(List<AnropsbehorighetBestallning> anropsbehorighet) {
        this.anropsbehorighet = anropsbehorighet;
    }

    public List<VagvalBestallning> getVagval() {
        return vagval;
    }

    public void setVagval(List<VagvalBestallning> vagval) {
        this.vagval = vagval;
    }
}
