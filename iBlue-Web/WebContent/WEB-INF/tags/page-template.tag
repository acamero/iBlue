<%@ attribute name="header" fragment="true"%>
<%@ attribute name="content" fragment="true"%>

<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->

<title>iBlue</title>

<!-- Bootstrap Core CSS -->
<link href="css/bootstrap.min.css" rel="stylesheet">

<!-- Custom CSS: You can use this stylesheet to override any Bootstrap styles and/or apply your own styles -->
<link href="css/custom.css" rel="stylesheet">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

<!-- Custom Fonts from Google -->
<link
	href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800'
	rel='stylesheet' type='text/css'>

<jsp:invoke fragment="header" />

</head>

<body>

	<!-- Navigation -->
	<nav id="siteNav" class="navbar navbar-default navbar-fixed-top"
		role="navigation">
		<div class="container">
			<!-- Logo and responsive toggle -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.jsp"> <span
					class="glyphicon glyphicon-globe"></span> iBlue
				</a>
			</div>
			<!-- Navbar links -->
			<div class="collapse navbar-collapse" id="navbar">
				<ul class="nav navbar-nav navbar-right">
					<li class="active"><a href="index.jsp">Home</a></li>
					<li><a href="index.jsp#download">Download</a></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button" aria-haspopup="true"
						aria-expanded="false">Publications <span class="caret"></span></a>
						<ul class="dropdown-menu" aria-labelledby="about-us">
							<li><a href="#">White paper</a></li>
							<li><a href="#">Parking Allocation</a></li>
							<li><a href="#">Other stuff</a></li>
						</ul></li>
					<li><a href="index.jsp#contact-us">Contact</a></li>
				</ul>

			</div>
			<!-- /.navbar-collapse -->
		</div>
		<!-- /.container -->
	</nav>

	<jsp:invoke fragment="content" />

	<!-- Footer -->
	<footer class="page-footer">

		<br /> <br />
		<!-- Copyright etc -->
		<div class="small-print">
			<div class="container">
				<a href="https://twitter.com/IblueParking">
					<img src="images/twitterbird.png" class="follow"/>
				</a> 
				<a href="https://www.facebook.com/100014161398184">
					<img src="images/facebook.png" class="follow"/>
				</a> 
				<a href="mailto:iblueparking@gmail.com">
					<img src="images/mail.png" class="follow"/>
				</a>
				<br/> <br/>
				<p>Copyright &copy; iBlue 2016</p>
			</div>
		</div>

	</footer>

	<!-- jQuery -->
	<script src="js/jquery-1.11.3.min.js"></script>

	<!-- Bootstrap Core JavaScript -->
	<script src="js/bootstrap.min.js"></script>

	<!-- Plugin JavaScript -->
	<script src="js/jquery.easing.min.js"></script>

	<!-- Custom Javascript -->
	<script src="js/custom.js"></script>

</body>

</html>