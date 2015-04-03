
<%@ page import="rage.DataSource" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Web Feed Discovery</title>
	</head>
	<body>
		<div class="container">
			<div class="row">
				<div class="span12">
			    	<h2>Web Feed Discovery</h2>
    			</div>
    		</div>
    		
    		<!--<div class="row">
				<div class="span12">
			    	<div class="alert alert-info">
			    		<p>
							Discover web feeds
						</p>
					</div>
    			</div>
    		</div>-->
    		
    		<div class="row">
				<div class="span12">
					<table class="table table-striped">
					<thead>
						<tr>
							<th></th>
							<th></th>
							<g:if test="${true == grailsApplication.config.rage.map.enable}"><th></th></g:if>
							<g:sortableColumn property="title" title="Feed" />
							<g:sortableColumn property="subtitle" title="Subtitle" />
							<g:sortableColumn property="lastRequest" title="Last Viewed" />
							<g:sortableColumn property="numTimesProcessed" title="Number of Viewings" />
						</tr>
					</thead>
					<tbody>
					<g:each in="${dataSourceInstanceList}" status="i" var="dataSourceInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							<td>
								<a href="${fieldValue(bean: dataSourceInstance, field: "primaryLink")}">
									<img src="${resource(dir:'images',file:'feed.jpg')}" style="height: 1.5em;">
								</a>
							</td>
							<td>
								<a href="${createLink(controller: 'googleEarthDashboard', action: 'kml', params: [feedUrl1: dataSourceInstance.primaryLink])}" class="btn btn-mini btn-info">
									<i class="icon-globe icon-white"></i>
								</a>
							</td>
							<g:if test="${true == grailsApplication.config.rage.map.enable}">
							<td>
								<a href="${createLink(controller: 'googleEarthDashboard', action: 'timemap', params: [feedUrl1: dataSourceInstance.primaryLink])}" class="btn btn-mini btn-primary">
									<i class="icon-picture icon-white"></i>
								</a>
							</td>
							</g:if>
							<td>${fieldValue(bean: dataSourceInstance, field: "title")}</td>
							<td>${fieldValue(bean: dataSourceInstance, field: "subtitle")}</td>
							<td>${fieldValue(bean: dataSourceInstance, field: "lastRequest")}</td>
							<td>${fieldValue(bean: dataSourceInstance, field: "numTimesProcessed")}</td>
						</tr>
					</g:each>
					</tbody>
					</table>
					<div class="pagination">
						<g:paginate total="${dataSourceInstanceTotal}" />
					</div>
				</div>
    		</div>
    		
		</div> <!-- close container -->
	</body>
</html>
