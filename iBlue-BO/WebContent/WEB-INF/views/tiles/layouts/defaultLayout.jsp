<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<c:url value="/resources/css/iblue-style.css" />">
<script src="<c:url value="/resources/js/common.js" />"></script>
<title><tiles:getAsString name="title" /></title>
</head>

<body>
	<div>
		<div id="header">
			<tiles:insertAttribute name="header" />
		</div>

		<div>
			<tiles:insertAttribute name="body" />
		</div>

		<div class="clear"></div>

		<div id="footer">
			<tiles:insertAttribute name="footer" />
		</div>
	</div>
</body>

</html>