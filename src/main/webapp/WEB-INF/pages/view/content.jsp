<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${empty monitorList}">
    <h3><spring:message code="message.noEntry"/></h3>
</c:if>
<c:if test="${not empty monitorList}">
	<c:forEach var="monitor" items="${monitorList}">
	    <div id="${monitor.id}-container" class="container ${monitor.status}">
	        <c:if test="${monitor.status == 'Pass'}">
	            <span id="${monitor.id}-status" class="status icon-checkmark"></span> 
	        </c:if>
	        <c:if test="${monitor.status == 'Failed'}">
	            <span id="${monitor.id}-status" class="status icon-close"></span> 
	        </c:if>
	        <c:if test="${monitor.status == 'Unknown'}">
	            <span id="${monitor.id}-status" class="status">
	                <img alt="Loading..." src="resources/images/loader.gif"/>
	            </span> 
	        </c:if>
	        
	        <span class="name">
	            <a href="${monitor.url}" title="${monitor.name}">${monitor.name}</a>
	        </span>
	        
	        <span class="time right">
	            <span id="${monitor.id}-lastCheck">
	                <c:if test="${not empty monitor.formattedLastCheck}">
	                    ${monitor.formattedLastCheck}
	                </c:if>
	                <c:if test="${empty monitor.formattedLastCheck}">
	                    <img alt="Loading..." src="resources/images/loader.gif"/>
	                </c:if>
	            </span>
	            <a href='#' class="more-info icon-chevron-down" title="More details" onclick="toggleDetails(this, ${monitor.id})"></a>
	        </span>
	        <div id="${monitor.id}-details" class="details">
	            <table>
	                <tr>
	                    <td class="header"><spring:message code="monitor.details.Description"/></td>
	                    <td><span class="value" title="${monitor.description}">${monitor.description}</span></td>
	                </tr>
	                <tr>
	                    <td class="header"><spring:message code="monitor.details.Url"/></td>
	                    <td><span class="value" title="${monitor.url}"><a href="${monitor.url}">${monitor.url}</a></span></td>
	                </tr>
	                <tr>
	                    <td class="header"><spring:message code="monitor.details.SearchText"/></td>
	                    <td><span class="value" title="${monitor.contentRegex}">${monitor.contentRegex}</span></td>
	                </tr>
	                <tr>
	                    <td class="header"><spring:message code="monitor.details.CronExpression"/></td>
	                    <td><span class="value" title="${monitor.cronExpression}">${monitor.cronExpression}</span></td>
	                </tr>
	                <tr>
	                    <td class="header"><spring:message code="monitor.details.Tag"/></td>
	                    <td>
	                        <c:if test="${not empty monitor.tag}"><span class="value" title="${monitor.tag}">${monitor.tag}</span></c:if>
	                    </td>
	                </tr>
	            </table>
	        </div>
	    </div>
	</c:forEach>
</c:if>