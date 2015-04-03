<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
	<Folder>
		<name>
			<g:if test="${folderName}"><![CDATA[${folderName}]]></g:if>
			<g:else>RAGE</g:else>
		</name>
		<description>
			<g:if test="${folderDescription}">${folderDescription}</g:if>
			<g:else>View syndication feeds from the comfort of GE</g:else>
		</description>
		<visibility>0</visibility>
		<open>0</open>
		<g:each in="${feeds}" status="i" var="feed">
			<NetworkLink>
				<name><![CDATA[${feed.title}]]></name>
				<description>
					<![CDATA[
					<table><tr><td bgcolor="#${feedColors.get(i)}" width="10%"></td><td><g:if test="${feedFilters.get(i).size() == 0}">${feed.description}</g:if><g:else>This feed is being filtered by: <g:each in="${feedFilters.get(i)}" var="filter">[${filter}] </g:each></g:else></td></tr></table>
					]]>
				</description>
				<visibility>0</visibility>
				<open>0</open>
				<refreshVisibility>0</refreshVisibility>
				<flyToView>0</flyToView>
				<Link>
					<href>
						<![CDATA[
							http://<g:rageServerURL />/feed/kml?<g:each in="${feedFilters.get(i)}" var="filter">${filter}&</g:each>color=${feedColors.get(i)}&url=<g:encodeParam>${feed.url}</g:encodeParam>
						]]>
					</href>
					<refreshMode>onInterval</refreshMode>
					<refreshInterval>300</refreshInterval>
				</Link>
			</NetworkLink>
		</g:each>
	</Folder>
</kml>
