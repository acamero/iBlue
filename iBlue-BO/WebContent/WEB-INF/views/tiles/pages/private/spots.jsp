<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="<c:url value="/resources/js/spots.js" />"></script>

<div id="left-map">
	<div id="map"></div>
</div>

<div id="right-map">


	<form id="update-spot" action="javascript:updateSpot();">
		<table>
			<tr>
				<th colspan="2">Posici&oacute;n</th>
			</tr>
			<tr>
				<td>Latitud:</td>
				<td><input type="text" name="latitude" id="latitude"
					value="" /></td>
			</tr>
			<tr>
				<td>Longitud:</td>
				<td><input type="text" name="longitude"
					id="longitude" value="" /></td>
			</tr>

			<tr>
				<th colspan="2">Propiedades</th>
			</tr>
			<tr>
				<td>Estado:</td>
				<td><input type="checkbox" name="status" id="status" value="1" checked/> Activo</td>
			</tr>	
			<tr>
				<td>Mac:</td>
				<td><input type="text" name="mac"
					id="mac" value="backoffice" /></td>
			</tr>			
		</table>
		
		<br /> <input type="submit" value="Actualizar" />
	</form>
</div>

<script async defer
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDyS6yLsY3WfcaI-mwmSwhyl1L-scSI2ck&callback=initMap">
	
</script>


