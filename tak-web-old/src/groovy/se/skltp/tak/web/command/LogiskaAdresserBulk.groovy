/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 *                  <http://cehis.se/>
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
package se.skltp.tak.web.command

import grails.validation.Validateable

@Validateable
class LogiskaAdresserBulk {

    
    /*

SE162321000156-3P4C, Vårdcentralen Hagfors
SE162321000255-O10553, Hudmottagning Centralsjukhuset Kristianstad
SE162321000255-O15994,  (Flexlab) Barnmorskemottagning Bromölla
SE162321000255-O16494,  (Flexlab) Barnmorskemottagning Familjens Hus Perstorp
SE162321000255-O16495,  (Flexlab) Barnmorskemottagning Familjens Hus Hässleholm
SE162321000255-O16499,  (Flexlab) Barnmorskemottagning Riksens Ständer Kristianstad
SE162321000255-O16559,  (Flexlab) Barnmorskemottagning Sjöbo
SE162321000255-O16560,  (Flexlab) Barnmorskemottagning Skurup
SE162321000255-O16561,  (Flexlab) Barnmorskemottagning Ystad
SE2321000016-12ZX, Solna Ungdomsmottagning
SE2321000016-1HZ3, (RTjP) Testspecialist-mottagning A
SE2321000016-TESTCHILD, Testmottagning Child
SE2321000016-TESTNYFT, Testmottagning Ny Drift
SE2321000057-5SXK, Blodcentralen Eksjö
SE2321000057-5VTN, (Cosmic) Tenhults vårdcentral
SE2321000057-5VZN, Lab Apladalens vårdcentral
SE2321000057-63D9, Utveckla Mina vårdkontakter
SE2321000115-O39423, BB/Förlossning Halmstad
SE2321000115-o40658, Laurentiuskliniken
SE2321000115-O41442, Läkemedelsgenomgång för äldre
SE2321000115-o42956, Vårdcentralen Kungsbacka
SE2321000115-O43026, Hälsa- och Rehabilitering Kungsbacka
SE2321000115-o99305, Vårdcentralen Tvååker
SE2321000131-E000000001059, Reumatolog mottagning Mölndal och Sahlgrenska
SE2321000131-E000000001760, Allergimottagning Sahlgrenska
SE2321000164-7381037591383, Hudmottagning USÖ
SE2321000164-7381037591420,  (RTjP) Infektionsmottagning USÖ
SE2321000164-7381037591833, Patientbokad gastroskopiundersökning
SE2321000164-7381037594353, Syncentralen
SE2321000164-7381037596050, Kvinnomottagning Karlskoga
SE2321000230-E00607, Cederkliniken

     */
        
    String logiskaAdresserBulk
    
    Map acceptedLines = [:]
    
    def rejectedLines = []
    
    
    
}
