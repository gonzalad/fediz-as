<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isErrorPage="true" import="java.io.*" session="false"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Error</title>
    <meta http-equiv="x-ua-compatible" content="ie=edge">
</head>
<body>
    <div class="error-panel">
        <h2 class="error-title">: (   Oops! Something went wrong here.</h2>
        <c:if test="${errorMsg == null}">
            <p class="error-desc">Due to an internal server error or misconfiguration, your request could not be completed.</p>
        </c:if>
        <c:if test="${errorMsg != null}">
            <p class="error-desc">${errorMsg}</p>
        </c:if>
        <p class="error-desc">Please contact your administrator.</p>
    </div>
    <!--
    Failed URL: ${url}
    Exception:  ${exception.message}
        <c:forEach items="${exception.stackTrace}" var="ste">    
            ${ste} 
        </c:forEach>
    Attributes: ${request.attributeNames}
    -->
</body>
</html>