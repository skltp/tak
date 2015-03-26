package se.skltp.tak.web.command

import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import java.sql.Date
import grails.validation.Validateable

@Validateable
class AnropsbehorighetBulk {
    
    String              integrationsavtal
    Tjanstekomponent    tjanstekonsument
    
    List tjanstekontrakts = [] // .withLazyDefault { return new Tjanstekontrakt() }
    
//  Tjanstekontrakt []  tjanstekontrakt = []
    Date                fromTidpunkt
    Date                tomTidpunkt
    
    // SE162321000156-3P4C, Vårdcentralen Hagfors
    // SE162321000156-3PR6, Vårdcentralen Rud
    // SE162321000156-3QFD, (Cosmic) Vårdcentralen Skåre)
    // .. ..
    String              logiskAdressBulk
    
    /*
     * 

HSA-VKK123
HSA-VKM345
HSA-VKY567
Ping
5565594230

HSA-VKK123, Demo adressat tidbok, vardcentralen kusten, Karna
HSA-VKM345, Demo adressat tidbok, vardcentralen kusten, Marstrand
HSA-VKY567, Demo adressat tidbok, vardcentralen kusten, Ytterby
Ping, VP's egna ping-tjanst
5565594230, Organisation: Inera

     * 
     */
    
    
    List rejectedLogiskAdress = []
    List logiskaAdresser = []
    
}
