<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<form method="get" action="<c:url value="/bulk/streets" />">
	<p>fromLat;fromLon;toLat;toLon[;capacity;type;status]</p>
	<p>36.716732;-4.482794;36.715984;-4.475295;50;0;1</p>
	<textarea id="bulk" name="bulk" ></textarea>
	<br/>
	<input type="submit" value="Procesar"/>
</form>


