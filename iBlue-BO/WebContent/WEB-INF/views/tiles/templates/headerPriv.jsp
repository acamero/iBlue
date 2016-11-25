<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="logo">
	<a href="home"><img src="resources/img/logo.png" /></a>
</div>
<div class="header-spacer"></div>
<div id="login-header-bullet" class="header-bullet">
	<a href="javascript:document.getElementById('logout').submit()">Salir</a>
	<c:url value="/logout" var="logoutUrl" />
	<form id="logout" action="${logoutUrl}" method="post">
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}" />
	</form>
</div>
<div id="spots-header-bullet" class="header-bullet">
	<a href="spots">Estado</a>
</div>
<div id="map-header-bullet" class="header-bullet">
	<a href="map">Mapa</a>
</div>
<div id="availability-priv-header-bullet" class="header-bullet">
	<a href="availability-priv">Disponibilidad</a>
</div>