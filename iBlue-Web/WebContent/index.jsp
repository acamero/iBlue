<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mycoolsite"%>
<mycoolsite:page-template>

	<jsp:attribute name="header">
    	<!-- Page specific JS / CSS goes here -->
 	</jsp:attribute>


	<jsp:attribute name="content">
    <!-- Header -->
    <header>
        <div class="header-content">
            <div class="header-content-inner">
                <h1>iBlue</h1>
                <p>Intelligent Parking Solution</p>
                <a href="#" class="btn btn-primary btn-lg">Demo</a>
            </div>
        </div>
    </header>

	<!-- Intro Section -->
    <section class="intro">
        <div class="container">
            <div class="row">
                <div class="col-lg-8 col-lg-offset-2">
                	<span class="glyphicon glyphicon-phone"
							style="font-size: 60px"></span>
                    <h2 class="section-heading">Intelligent Parking Solution</h2>
                    <p class="text-light">
			Blah, blah, blah... briefly describe our proposal.
		    </p>
                </div>
            </div>
        </div>
    </section>

	<!-- Content 1 -->
    <section class="content">
        <div class="container">
            <div class="row">
                <div class="col-sm-6">
                    <img class="img-responsive img-circle center-block"
							src="images/us.jpg" alt="">
                </div>
                <div class="col-sm-6">
                	<h2 class="section-header">Our Goal</h2>
                	<p class="lead text-muted">
			    What are we trying to do...
			</p>
                	
                </div>                
                
            </div>
        </div>
    </section>
    
    <!-- Promos -->
    <div class="container-fluid">
        <div class="row promo">
        <div class="col-md-4 promo-item item-1">
        	<h3>
	            <a href="#">			
							White Paper				
	            </a>
            </h3>
		</div>
            <div class="col-md-4 promo-item item-2">
            <h3>
	            <a href="#">			
							Parking Allocation				
	            </a>
            </h3>
			</div>
			<div class="col-md-4 promo-item item-3">
	    	<h3>
	            <a href="#">			
							Other Stuff				
	            </a>
            </h3>
            </div>
        </div>
    </div>
		<!-- /.container-fluid -->

	<!-- Content 2 -->
     <section class="content content-2">
        <div class="container" id="download">
            <div class="row">
                <div class="col-sm-6">
                	<h2 class="section-header">Download</h2>
                	<p class="lead text-light">
			    Introduce the software...
			</p>
			<a href="#" class="btn btn-primary btn-lg">Download</a>
                </div>    
                <div class="col-sm-6">
                    <img class="img-responsive img-circle center-block"
							src="images/iphone.jpg" alt="">
                </div>            
                
            </div>
        </div>
    </section>    

    

	
  </jsp:attribute>

</mycoolsite:page-template>