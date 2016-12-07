<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="<c:url value="/resources/js/map.js" />"></script>

<div id="left-map">
	<div id="map"></div>
	<div><a href="javascript:reload();">Cargar posici&oacute;n</a></div>
</div>

<div id="right-map">


	<form id="update-street" action="javascript:updateStreet();">
		<table>
			<tr>
				<th colspan="2">Desde</th>
			</tr>
			<tr>
				<td>Latitud:</td>
				<td><input type="text" name="from-latitude" id="from-latitude"
					value="" /></td>
			</tr>
			<tr>
				<td>Longitud:</td>
				<td><input type="text" name="from-longitude"
					id="from-longitude" value="" /></td>
			</tr>

			<tr>
				<th colspan="2">Hasta</th>
			</tr>
			<tr>
				<td>Latitud:</td>
				<td><input type="text" name="to-latitude" id="to-latitude"
					value="" /></td>
			</tr>
			<tr>
				<td>Longitud:</td>
				<td><input type="text" name="to-longitude" id="to-longitude"
					value="" /></td>
			</tr>

			<tr>
				<th colspan="2">Propiedades</th>
			</tr>
			<tr>
				<td>Estado:</td>
				<td><input type="checkbox" name="status" id="status" value="1" checked/> Activo</td>
			</tr>
			<tr>
				<td>Capacidad:</td>
				<td><input type="text" name="capacity" id="capacity" value="" /></td>
			</tr>
			<tr>
				<td>Tipo:</td>
				<td><input type="text" name="type" id="type" value="" /></td>
			</tr>
		</table>
		<input type="hidden" id="street-id" />
		<br /> <input type="submit" value="Actualizar" />
	</form>
	<br/>
	<a href="<c:url value="/bo/bulk-load-streets" />" >Carga masiva de calles</a>
</div>

<script async defer
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDyS6yLsY3WfcaI-mwmSwhyl1L-scSI2ck&callback=initMap">
	
</script>

