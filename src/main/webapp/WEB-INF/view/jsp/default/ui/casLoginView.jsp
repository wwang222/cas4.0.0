<!DOCTYPE html>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head lang="zh-cn">
  <meta charset="UTF-8">
  <title>CAS单点登录开箱即用</title>
  <spring:theme code="standard.custom.css.file" var="customCssFile" />
  <link rel="stylesheet" href="<c:url value="${customCssFile}" />" />
  <link rel="icon" href="<c:url value="/favicon.ico" />" type="image/x-icon" />
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- MAIN -->
  <link href="styles/login.css" rel="stylesheet" type="text/css">
</head>

<body>
<div class="login-wrapper">
  <div class="login-hd cf">
    <div class="login-logo pull-left">
      <h1>CAS单点登录开箱即用</h1>
    </div>
    <div class="login-menu pull-right">
      <a href="https://www.apereo.org/">CAS官网</a>
      <span class="divider">|</span>
      <a href="http://blog.csdn.net/ii_bat">技术支撑</a>
    </div>
  </div>

  <div class="login-bd cf">
    <form:form method="post" id="fm1" commandName="${commandName}" htmlEscape="true">
      <c:if test="${not pageContext.request.secure}">
        <div id="msg" class="errors">
          <h2>Non-secure Connection</h2>
          <p>You are currently accessing CAS over a non-secure connection.  Single Sign On WILL NOT WORK.  In order to have single sign on work, you MUST log in over HTTPS.</p>
        </div>
      </c:if>
      <div class="login-form pull-right">

        <div class="login-form-hd">
          用户登录
        </div>
        <div class="login-form-bd">
          <form:errors path="*" id="msg" cssClass="errors" element="div" htmlEscape="false"/>
          <div class="row">
            <c:choose>
              <c:when test="${not empty sessionScope.openIdLocalId}">
                <strong>${sessionScope.openIdLocalId}</strong>
                <input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}" />
              </c:when>
              <c:otherwise>
                <spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
                <form:input cssClass="required q" cssErrorClass="error" id="username" size="10" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="off" htmlEscape="true" placeholder="用户名" />
              </c:otherwise>
            </c:choose>

          </div>
          <div class="row">
            <spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
            <form:password cssClass="required q" cssErrorClass="error" id="password" size="10" placeholder="密码" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />
          </div>
          <div class="row">
            <input type="hidden" name="lt" value="${loginTicket}" />
            <input type="hidden" name="execution" value="${flowExecutionKey}" />
            <input type="hidden" name="_eventId" value="submit" />
            <button class="btn btn-primary">登录</button>
          </div>
          <div class="tip">
            <p>
              <strong>提示</strong>
              <br>
              如若出现：you MUST log in over HTTPS，请克隆一份支持https的运行环境。
            </p>
          </div>
        </div>
      </div>
    </form:form>
  </div>
</div>
<script type="text/javascript" src="/cas/js/jquery.min.js"></script>
<script type="text/javascript" src="/cas/js/jquery-ui.min.js"></script>
<script src="/cas/js/cas.js" type="text/javascript"></script>
</body>
</html>