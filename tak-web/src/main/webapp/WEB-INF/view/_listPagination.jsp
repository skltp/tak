<div class="pagination">
    <c:forEach begin="1" end="${list.totalPages}" var="p">
        <c:choose>
            <c:when test="${p == list.number}">
                <span class="currentStep">${p}</span>
            </c:when>
            <c:when test="${p == 1 || p == list.totalPages}">
                <a href="${basePath}?offset=${list.max*(p-1)}&max=${list.max}" class="step">${p}</a>
            </c:when>
            <c:when test="${p == list.number-5 || p == list.number+5}">
                <span class="step gap">..</span>
            </c:when>
            <c:when test="${p < list.number-5 || p > list.number+5}">
            </c:when>
            <c:otherwise>
                <a href="${basePath}?offset=${list.max*(p-1)}&max=${list.max}" class="step">${p}</a>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>