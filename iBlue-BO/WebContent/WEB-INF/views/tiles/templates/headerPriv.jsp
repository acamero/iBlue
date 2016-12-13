<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="logo">
	<a href="<c:url value="/bo/home"/>"><img src="<c:url value="/resources/img/logo.png"/>" /></a>
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
	<a href="<c:url value="/bo/spots" />">Estado</a>
</div>

<div id="map-header-bullet" class="header-bullet">
	<a href="<c:url value="/bo/map" />">Mapa</a>
</div>

<div id="availability-header-bullet" class="header-bullet">
	<a href="<c:url value="/bo/availability" />">Disponibilidad</a>
</div>

<div id="route-header-bullet" class="header-bullet">
	<a href="<c:url value="/bo/route" />">Ruta</a>
</div>