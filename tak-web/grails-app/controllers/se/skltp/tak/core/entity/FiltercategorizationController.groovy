/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 					<http://cehis.se/>
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
package se.skltp.tak.core.entity

import org.grails.plugin.filterpane.FilterPaneUtils
import org.apache.commons.logging.LogFactory

class FiltercategorizationController extends AbstractController {

    private static final log = LogFactory.getLog(this)

    def scaffold = Filtercategorization

    def entityLabel = { message(code: 'filtercategorization.label', default: 'Filtercategorization') }

    public String getEntityLabel() {
        return entityLabel()
    }
    public void onDeleteEntityAction(AbstractVersionInfo entityInstance) {
        def filter = entityInstance.filter
        filter.removeFromCategorization(entityInstance)
    }

    public Class getEntityClass() {
        Filtercategorization
    }
    public AbstractVersionInfo createEntity(params) {
        new Filtercategorization(params)
    }
    public LinkedHashMap<String, AbstractVersionInfo> getModel(entityInstance) {
        [filtercategorizationInstance: entityInstance]
    }
    public ArrayList<AbstractVersionInfo> getEntityDependencies(entityInstance) {
        List<AbstractVersionInfo> entityList = new ArrayList<AbstractVersionInfo>();
        //No constraints yet
        entityList
    }

    def filterPaneService

    def filter() {
        render( view:'list',
                model:[ filtercategorizationInstanceList: filterPaneService.filter( params, Filtercategorization),
                        filtercategorizationInstanceTotal: filterPaneService.count( params, Filtercategorization ),
                        filterParams: FilterPaneUtils.extractFilterParams(params),
                        params:params ] )
    }
}
