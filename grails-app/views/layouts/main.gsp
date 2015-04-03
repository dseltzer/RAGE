<!DOCTYPE html> 
<html>
    <head>
        <title><g:layoutTitle default="RAGE" /></title>
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <r:require modules="bootstrap-css"/>
		<g:layoutHead />
		<r:layoutResources/>		
    </head>
    <body>
		<div class="navbar navbar-static-top">
			<div class="navbar-inner">
				<div class="container">
					<a class="brand" href="${resource(dir:'')}">RAGE</a>
					<div class="nav-collapse collapse">
						<ul class="nav">
							<li><a href="${createLink(controller: 'googleEarthDashboard', action: 'create')}"><i class="icon-globe"></i> Visualize</a></li>
							<g:if test="${true == grailsApplication.config.rage.discovery.enable}">
								<li><a href="${createLink(controller: 'dataSource', action: 'list')}"><i class="icon-search"></i> Discover</a></li>
							</g:if>
						</ul>
					</div><!--/.nav-collapse -->
				</div>
			</div>
		</div>
		
        <g:layoutBody />
        <r:layoutResources/>	
    </body>	
</html>