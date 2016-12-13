<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="<c:url value="/resources/js/routes.js" />"></script>

<div id="left-map">
	<div id="map"></div>
</div>

<div id="right-map">


	<form id="get-route" action="javascript:getRoute();">
		<table>
			<tr>
				<th colspan="2">Origen</th>
			</tr>
			<tr>
				<td>Latitud:</td>
				<td><input type="text" name="latitude1" id="latitude1" value="" /></td>
			</tr>
			<tr>
				<td>Longitud:</td>
				<td><input type="text" name="longitude1" id="longitude1"
					value="" /></td>
			</tr>
			<tr>
				<th colspan="2">Destino</th>
			</tr>
			<tr>
				<td>Latitud:</td>
				<td><input type="text" name="latitude2" id="latitude2" value="" /></td>
			</tr>
			<tr>
				<td>Longitud:</td>
				<td><input type="text" name="longitude2" id="longitude2"
					value="" /></td>
			</tr>

		</table>

		<br /> <input type="submit" value="Actualizar" />
	</form>
</div>

<script async defer
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDyS6yLsY3WfcaI-mwmSwhyl1L-scSI2ck&callback=initMap">
	
</script>


