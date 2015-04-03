<?xml version="1.0" encoding="UTF-8"?>
<%! import rage.Geometry %>
<kml xmlns="http://www.opengis.net/kml/2.2">
<Document>
	<g:if test="${flash.message}">
           	<Placemark>
			<name>ERROR</name>
			<description>${flash.message}</description>
		</Placemark>
	</g:if>
	<g:else>
		<Style id="<g:rgbToBGR>${feedColor}</g:rgbToBGR>_normal">
			<LabelStyle>
				<color>00ffffff</color>
			</LabelStyle>
			<IconStyle>
				<color>ff<g:rgbToBGR>${feedColor}</g:rgbToBGR></color>
				<scale>1</scale>
				<Icon>
					<href>http://maps.google.com/mapfiles/kml/paddle/wht-blank.png</href>
 				</Icon>
			</IconStyle>
			<LineStyle>
				<color>ff<g:rgbToBGR>${feedColor}</g:rgbToBGR></color>
				<width>2.0</width>
			</LineStyle>
			<PolyStyle>
				<!-- Set opacity to transparent and fill to true to ensure large polygons outlines are always visible, but no fill-->
				<color>00<g:rgbToBGR>${feedColor}</g:rgbToBGR></color>
				<colorMode>normal</colorMode>
				<fill>true</fill>
				<outline>true</outline>
			</PolyStyle>
			<BalloonStyle>
				<text>$[description]</text>
			</BalloonStyle>
		</Style>
		<Style id="<g:rgbToBGR>${feedColor}</g:rgbToBGR>_highlighted">
			<LabelStyle>
				<color>ffffffff</color>
			</LabelStyle>
			<IconStyle>
				<color>ff<g:rgbToBGR>${feedColor}</g:rgbToBGR></color>
				<scale>1.4</scale>
				<Icon>
					<href>http://maps.google.com/mapfiles/kml/paddle/wht-blank.png</href>
 				</Icon>
			</IconStyle>
			<LineStyle>
				<color>ff<g:rgbToBGR>${feedColor}</g:rgbToBGR></color>
				<width>2.0</width>
			</LineStyle>
			<PolyStyle>
				<!-- Set opacity to transparent and fill to true to ensure large polygons outlines are always visible, but no fill-->
				<color>00<g:rgbToBGR>${feedColor}</g:rgbToBGR></color>
				<colorMode>normal</colorMode>
				<fill>true</fill>
				<outline>true</outline>
			</PolyStyle>
			<BalloonStyle>
				<text>$[description]</text>
			</BalloonStyle>
		</Style>
		<StyleMap id="<g:rgbToBGR>${feedColor}</g:rgbToBGR>">
			<Pair>
				<key>normal</key>
				<styleUrl>#<g:rgbToBGR>${feedColor}</g:rgbToBGR>_normal</styleUrl>
			</Pair>
			<Pair>
				<key>highlight</key>
				<styleUrl>#<g:rgbToBGR>${feedColor}</g:rgbToBGR>_highlighted</styleUrl>
			</Pair>
		</StyleMap>
		<g:each in="${feedInstance.placemarks}" var="mapItem">
			<g:if test="${mapItem.value.size() > 1}">
				<Folder>
					<name><![CDATA[${mapItem.key} [${mapItem.value.size()}]]]></name> 
			</g:if>
			<g:each in="${mapItem.value}" status="i" var="placemarkInstance">
				<g:if test="${placemarkInstance.enclosures}">
					<Folder>
						<name><![CDATA[${placemarkInstance.name}]]></name>
				</g:if>
				<Placemark>
					<name><![CDATA[${placemarkInstance.name}]]></name>
					<description>
						<![CDATA[${placemarkInstance.description}
							<g:if test="${placemarkInstance.enclosures}">
								<g:each in="${placemarkInstance.enclosures}" var="enclosure">
									</br><a href="${enclosure.url}">${enclosure.title}</a>
								</g:each>
							</g:if>
						]]>
					</description>
					<styleUrl>#<g:rgbToBGR>${feedColor}</g:rgbToBGR></styleUrl>
					<g:if test="${placemarkInstance.timestamp}">
						<TimeStamp>
							<when>${placemarkInstance.timestamp}</when>
						</TimeStamp>
					</g:if>
					<g:if test="${placemarkInstance.geometryList == null}">
						<Snippet>
							No geolocation data available!
						</Snippet>
					</g:if>
					<g:elseif test="${placemarkInstance.geometryList.size() >= 1}">
						<Snippet/>
						<MultiGeometry>
							<g:each in="${placemarkInstance.geometryList}" status="j" var="geometryInstance">
								<g:if test="${geometryInstance.type == Geometry.Type.POINT}">
									<Point>
										<coordinates>${geometryInstance.points.get(0).getX()},${geometryInstance.points.get(0).getY()},0</coordinates>
									</Point>
								</g:if>
								<g:elseif test="${geometryInstance.type == Geometry.Type.POLYGON}">
									<Point>
										<coordinates>${geometryInstance.points.get(0).getX()},${geometryInstance.points.get(0).getY()},0</coordinates>
									</Point>
									<g:each in="${geometryInstance.splitOnDateLine()}" var="splitGeometryPointList">
										<Polygon>
											<outerBoundaryIs>
												<LinearRing>
													<coordinates>
														<g:each in="${splitGeometryPointList}" var="pointInstance">
															${pointInstance.getX()},${pointInstance.getY()},0</g:each>
													</coordinates>
												</LinearRing>
											</outerBoundaryIs>
										</Polygon>
									</g:each>
								</g:elseif>
							</g:each>
						</MultiGeometry>
					</g:elseif>
					<g:else>
						<!-- placemark with empty geometryList, nothing to do -->
					</g:else>
				</Placemark>
				<g:if test="${placemarkInstance.enclosures}">
					<g:each in="${placemarkInstance.enclosures}" var="enclosure">
						<g:if test="${enclosure.type.contains('application/vnd.google-earth.kml+xml')}">
							<NetworkLink>
								<name><![CDATA[${enclosure.title}]]></name>
								<visibility>0</visibility>
								<open>0</open>
								<flyToView>0</flyToView>
								<Link>
									<href><![CDATA[${enclosure.url}]]></href>
								</Link>
							</NetworkLink>
						</g:if>
						<g:elseif test="${enclosure.type.contains('application/atom+xml')}">
							<NetworkLink>
								<name><![CDATA[${enclosure.title}]]></name>
								<visibility>0</visibility>
								<open>0</open>
								<flyToView>0</flyToView>
								<Link>
									<href><![CDATA[http://<g:rageServerURL />/feed/kml?url=<g:encodeParam>${enclosure.url}</g:encodeParam>]]></href>
								</Link>
							</NetworkLink>
						</g:elseif>
					</g:each>
					</Folder>
				</g:if>
			</g:each> <!-- list of placemarks -->
			<g:if test="${mapItem.value.size() > 1}">
				</Folder>
			</g:if>
		</g:each> <!-- map of lists -->
	</g:else>
	</Document>
</kml>