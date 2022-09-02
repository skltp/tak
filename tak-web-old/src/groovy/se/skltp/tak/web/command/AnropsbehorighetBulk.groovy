package se.skltp.tak.web.command

import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import java.sql.Date
import grails.validation.Validateable

@Validateable
class AnropsbehorighetBulk {
    
    String            integrationsavtal
    Tjanstekomponent  tjanstekonsument
    
    List              tjanstekontrakts = []

    Date              fromTidpunkt
    Date              tomTidpunkt
    
    String            logiskAdressBulk
    List              rejectedLogiskAdress = []
    List              logiskaAdresser = []

    /*

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

     */
}
