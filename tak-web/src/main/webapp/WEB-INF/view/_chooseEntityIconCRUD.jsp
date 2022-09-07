<c:choose>
    <c:when test="${entity.isNewlyCreated()}">
        <img src="/static/images/created.png" alt="Skapad" />
    </c:when>
    <c:when test="${entity.isUpdatedAfterPublishedVersion()}">
        <img src="/static/images/updated.png" alt="Uppdaterad" />
    </c:when>
    <c:when test="${entity.getDeleted()}">
        <img src="/static/images/trash.png" alt="Borttagen" />
    </c:when>
</c:choose>
