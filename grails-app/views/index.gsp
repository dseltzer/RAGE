<%--
Copyright (C) 2008-2010 United States Government. All rights reserved. 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
--%>
<html>
	<head>
		<meta name="layout" content="main" />
		<title>Welcome to RAGE</title>
		<style>
			.deemphasized { color: #444; font-size: 0.7em; }
    	</style>
	</head>
	<body>
		<div class="container">
			<div class="hero-unit">
				<h1>Welcome to RAGE <small>RSS/Atom in Google Earth</small></h1>
				<p>
					Web feeds are a popular mechanism for publishing frequently updated content, such as news articles.
					Google Earth does not support web feeds despite the fact they often contain geospatial information.
					Use RAGE to <strong>bridge the gap between geo-tagged web feeds and Google Earth</strong>. 
				</p>
				<p>
					<g:link controller="googleEarthDashboard" action="create" class="btn btn-large btn-primary">Get Started</g:link>
				</p>
			</div>
    
           	<div class="row-fluid">
           		    <!--<ul class="thumbnails">
    					 <li class="span4">
							<div class="thumbnail">
								<img data-src="${resource(dir:'images',file:'googleEarthWithFeed.jpg')}" alt="">
								<h3>Visualize</h3>
								<p>View web feeds with the geospatial visualization power of Google Earth and slippy maps.</p>
							</div>
						</li>
    				</ul>-->
      			<div class="span4">
        			<img src="${resource(dir:'images',file:'googleEarthWithFeed.png')}" style="height: 12em; padding:2px 0 2px 0;">
        			<h2>Visualize</h2>
        			<p>Harness the geospatial visualization power of Google Earth to turn web feeds into beautifully rendered KML.</p>
        			<p class="deemphasized">The above Google Earth screen capture demonstrates the USGS M 1.0+ Earthquakes Atom feed viewed with RAGE.</p>
      			</div>
      			<div class="span4">
        			<img src="${resource(dir:'images',file:'mashup.png')}" style="height: 12em; padding:2px 0 2px 0;">
        			<h2>Mashup</h2>
        			<p>View multiple web feeds at the same time to understand proximity and gain insights from different data sources.</p>
        		</div>
        		<g:if test="${true == grailsApplication.config.rage.discovery.enable}">
      			<div class="span4">
        			<img src="${resource(dir:'images',file:'discovery.png')}" style="height: 12em; border: 2px solid black;">
        			<h2>Discover</h2>
        			<p>Interested in web feeds available to the community? Use the Discovery feature to browse web feeds that have been transformed by RAGE into KML.</p>
        		</div>
        		</g:if>
			</div>
    	</div>
	
		<!-- Need to implement database before this piece is useful
		<div id="homeCell">
			<h3>Collaborate</h3>
			<p>
			RAGE tracks all feeds fetched	allowing for discover, mashups, and collaboration.
			</p>
			<span class="buttons">
				<g:link controller="feed" action="list">View Feeds</g:link>
			</span>
		</div>-->
	</body>
</html>
