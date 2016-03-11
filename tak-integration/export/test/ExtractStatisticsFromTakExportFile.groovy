/**
 * Script helps comparing TAK-export files in JSON-format.
 *
 *
 * Run script using:
 *  groovy ExtractStatisticsFromTakExportFile.groovy <file>
 */

import groovy.json.*

File file = new File(this.args[0])

def jsonSlurper = new JsonSlurper();
def jsonRoot = jsonSlurper.parseText(file.text)

collectStatistics(jsonRoot)

def collectStatistics(Map jsonRoot) {

    SortedMap entityMap = new TreeMap()
    Map entityToIdListMap = new HashMap()
    jsonRoot.data.each { entity_name ->
        entityMap.put(entity_name.key, entity_name.value.size())
        List idList = new ArrayList()
        entityToIdListMap.put(entity_name.key, idList)
        entity_name.value.each { entityInstance ->
            idList.add(entityInstance.id)
        }
    }

    // print stats overview
    entityMap.each { entity_name ->
        println "--- entity count ---"
        println entity_name.key + ": " + entity_name.value
    }

    // print stats details
    entityMap.each { entity_name ->
        println "--- " + entity_name.key + " id's ---"
        Collections.sort(entityToIdListMap.get(entity_name.key))
        entityToIdListMap.get(entity_name.key).value.each {
            println entity_name.key + ".id: " + it
        }
    }
}
