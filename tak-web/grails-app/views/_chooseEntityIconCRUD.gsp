    <g:if test="${entity.isNewlyCreated()}">
        <img src="${resource(dir:'images',file:'created.png')}" alt="Skapad" />
    </g:if>
    <g:elseif test="${entity.isUpdatedAfterPublishedVersion()}">
        <img src="${resource(dir:'images',file:'updated.png')}" alt="Uppdaterad" />
    </g:elseif>
    <g:elseif test="${entity.getDeleted()}">
        <img src="${resource(dir:'images',file:'trash.png')}" alt="Borttagen" />
    </g:elseif>

