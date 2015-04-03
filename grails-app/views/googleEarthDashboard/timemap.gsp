<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="layout" content="main" />
    <title>RAGE</title>
    <style>
		div#timelinecontainer { height: 150px; }
		div#mapcontainer { height: 550px; }
		div.olFramedCloudPopupContent { width: 300px; }
    </style>
  </head>
<body>
	<div class="container">
	
  		<g:each in="${feedUrls}" status="i" var="feedUrl">
		<div class="row">
			<div class="span12">
  				<img class="iconLegend" src="${createLinkTo(dir: "images", file: "")}/<g:rgbToIcon rgb="${feedColors.get(i)}"/>" />
  				${feedUrl}
  			</div>
  		</div>
  		</g:each>
  		
  		<div class="row">
			<div class="span12">
    			<div id="timelinecontainer">
          			<div id="timeline"></div>
        		</div>
       			<div id="mapcontainer">
          			<div id="map"></div>
				</div>
			</div>
		</div>
		
	</div>
	
	<g:javascript src="jquery/jquery-1.8.3.js" />
    <g:javascript src="openlayers/OpenLayers.js" />
    <r:require module="timemap"/>
    <script type="text/javascript">
var tm;
$(function() {
	TimeMap.themes.rage_white = TimeMapTheme.create(
		"ffffff",
		{
			eventColor:"#ffffff",
			fillColor: "#ffffff",
			lineColor: "#ffffff",
			icon:"${createLinkTo(dir: "images", file: "wht-circle.png")}"
		}
	);
	TimeMap.themes.rage_red = TimeMapTheme.create(
		"ff0000",
		{
			eventColor:"#ff0000",
			fillColor: "#ff0000",
			lineColor: "#ff0000",
			icon:"${createLinkTo(dir: "images", file: "red-circle.png")}"
		}
	);
	TimeMap.themes.rage_yellow = TimeMapTheme.create(
		"ffff00",
		{
			eventColor:"#ffff00",
			fillColor: "#ffff00",
			lineColor: "#ffff00",
			icon:"${createLinkTo(dir: "images", file: "ylw-circle.png")}"
		}
	);
	TimeMap.themes.rage_green = TimeMapTheme.create(
		"00ff00",
		{
			eventColor:"#00ff00",
			fillColor: "#00ff00",
			lineColor: "#00ff00",
			icon:"${createLinkTo(dir: "images", file: "grn-circle.png")}"
		}
	);
	TimeMap.themes.rage_lightblue = TimeMapTheme.create(
		"00ffff",
		{
			eventColor:"#00ffff",
			fillColor: "#00ffff",
			lineColor: "#00ffff",
			icon:"${createLinkTo(dir: "images", file: "ltblu-circle.png")}"
		}
	);
	TimeMap.themes.rage_blue = TimeMapTheme.create(
		"0000ff",
		{
			eventColor:"#0000ff",
			fillColor: "#0000ff",
			lineColor: "#0000ff",
			icon:"${createLinkTo(dir: "images", file: "blu-circle.png")}"
		}
	);
	TimeMap.themes.rage_purple = TimeMapTheme.create(
		"cc00ff",
		{
			eventColor:"#cc00ff",
			fillColor: "#cc00ff",
			lineColor: "#cc00ff",
			icon:"${createLinkTo(dir: "images", file: "purple-circle.png")}"
		}
	);
	TimeMap.themes.rage_pink = TimeMapTheme.create(
		"ff00ff",
		{
			eventColor:"#ff00ff",
			fillColor: "#ff00ff",
			lineColor: "#ff00ff",
			icon:"${createLinkTo(dir: "images", file: "pink-circle.png")}"
		}
	);

	var windowWidth = $(window).width();
	if (windowWidth === 0) windowWidth = 1200; //IE returns 0 sometimes - this causes big problems!
    tm = TimeMap.init({
        mapId: "map",               // Id of map div element (required)
        timelineId: "timeline",     // Id of timeline div element (required)
        datasets: [
        	<g:each in="${feedUrls}" status="i" var="feedUrl">
            {
                id: "feed${i}",
                title: "feed${i}",
                theme: '<g:rgbToTheme rgb="${feedColors.get(i)}"/>',
                type: "kml",
                options: {
                    url: "${createLink(controller: 'feed', action: 'kml', params: ['color': 'ffffff' , 'url': feedUrl])}"
                }
            }<g:if test="${i < (feedUrls.size() - 1)}">,</g:if>
            </g:each>
        ],
        bandInfo: [
        	{
            	width:			"75%",
            	intervalUnit:	Timeline.DateTime.HOUR, 
            	intervalPixels:	windowWidth
            } ,
            {
            	width:			"25%",
            	intervalUnit:	Timeline.DateTime.DAY, 
            	intervalPixels:	windowWidth/3,
            	showEventText:	false,
            	overview:		true,
            	trackHeight:	0.4,
            	trackGap:		0.2
            }
        ]
    });
    
    <g:if test="${grailsApplication.config.rage.openlayrs.map.xyz.url}">
    	var osm = tm.getNativeMap();
    	
    	//remove old base layer
    	var defaultBaseLayers = osm.getLayersByClass("OpenLayers.Layer.TMS");
    	if (defaultBaseLayers.length > 0) osm.removeLayer(defaultBaseLayers[0]);
    	
    	//add new base layer
    	var name = "${grailsApplication.config.rage.openlayrs.map.xyz.url}";
    	<g:if test="${grailsApplication.config.rage.openlayrs.map.xyz.name}">
    		name = "${grailsApplication.config.rage.openlayrs.map.xyz.name}";
    	</g:if>
    	var osmLayer = new OpenLayers.Layer.XYZ(name, "${grailsApplication.config.rage.openlayrs.map.xyz.url}");
    	osm.addLayer(osmLayer);
    	osm.setBaseLayer(osmLayer);
    	osm.setLayerIndex(osmLayer, 0);
    </g:if>
});
    </script>
</body>
</html>
