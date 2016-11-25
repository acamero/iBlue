<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>



<div id="login">

	<c:if test="${not empty error}">
		<div class="error">${error}</div>
	</c:if>
	<c:if test="${not empty message}">
		<div class="error">${message}</div>
	</c:if>

		
	<form name='login' action="<c:url value='./login' />" method='POST'>
		<table>
			<tr>
				<td>Usuario:</td>
				<td><input type='text' name='username' value=''></td>
			</tr>
			<tr>
				<td>Contrase&ntilde;a:</td>
				<td><input type='password' name='password' /></td>
			</tr>
			<tr>
				<td colspan='2'><input name="submit" type="submit"
					value="Entrar" /></td>
			</tr>
		</table>
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}" />
	</form>
</div>