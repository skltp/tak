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

public class BestallningsAvsnitt {
    private List<TjanstekontraktBestallning> tjanstekontrakt = null;
    private List<LogiskadressBestallning> logiskadresser = null;
    private List<TjanstekomponentBestallning> tjanstekomponenter = null;
    private List<AnropsbehorighetBestallning> anropsbehorigheter = null;
    private List<VagvalBestallning> vagval = null;

    List<TjanstekontraktBestallning> getTjanstekontrakt() {
        return tjanstekontrakt
    }

    void setTjanstekontrakt(List<TjanstekontraktBestallning> tjanstekontrakt) {
        this.tjanstekontrakt = tjanstekontrakt
    }

    List<LogiskadressBestallning> getLogiskadresser() {
        return logiskadresser
    }

    void setLogiskadresser(List<LogiskadressBestallning> logiskadresser) {
        this.logiskadresser = logiskadresser
    }

    List<TjanstekomponentBestallning> getTjanstekomponenter() {
        return tjanstekomponenter
    }

    void setTjanstekomponenter(List<TjanstekomponentBestallning> tjanstekomponenter) {
        this.tjanstekomponenter = tjanstekomponenter
    }

    List<AnropsbehorighetBestallning> getAnropsbehorigheter() {
        return anropsbehorigheter
    }

    void setAnropsbehorigheter(List<AnropsbehorighetBestallning> anropsbehorigheter) {
        this.anropsbehorigheter = anropsbehorigheter
    }

    List<VagvalBestallning> getVagval() {
        return vagval
    }

    void setVagval(List<VagvalBestallning> vagval) {
        this.vagval = vagval
    }
}
