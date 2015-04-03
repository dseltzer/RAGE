/**************************************************************************
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
****************************************************************************/
package rage

import rage.filters.Filter
import rage.filters.BoundingBoxGeoFilter
import rage.filters.NumericalFilter
import rage.filters.StringFilter
import rage.utils.ConverterUtils
import rage.utils.HttpUtils

class FeedController {
    
	def grailsApplication
	def dataSourceService
	def feedParserService
	
	def kml = {

		String feedUrl = params.url
		if (!feedUrl) {
			log.error("Required parameter 'url' not provided, params: " + params)
			flash.message = "Feed URL not provided, unable to GET feed!"
			render(contentType: "application/vnd.google-earth.kml+xml", view:"kml")
			return
		}
		String color = params.color;
		if (!color) {
			color = grailsApplication.config.rage.default.marker.color
			log.debug("Parameter 'color' not provided, setting to default: " + color);
		}

		feedUrl = ConverterUtils.decodeParam(feedUrl)
		Map requestMap = HttpUtils.getFeed(feedUrl, null,  null)

		Feed feed = new Feed()
		if (200 == requestMap.get('responsecode')) {
			List<Filter> filters = []
			if (params.title) {
				String[] args = params.title.split("_")
				filters << StringFilter.instanceOfTitleFilter(args[0], args[1])
			}
			if (params.content) {
				String[] args = params.content.split("_")
				filters << StringFilter.instanceOfContentFilter(args[0], args[1])
			}
			if (params."${NumericalFilter.PARAM_NAME}") {
				filters << NumericalFilter.instanceOfUrlString(params."${NumericalFilter.PARAM_NAME}")
			}
			if (params."${BoundingBoxGeoFilter.PARAM_NAME}") {
				filters << BoundingBoxGeoFilter.instanceOfUrlString(params."${BoundingBoxGeoFilter.PARAM_NAME}")
			}
			feed = feedParserService.parseFeed(requestMap.get('content'), filters)
			if (true == grailsApplication.config.rage.discovery.enable) {
				feed.url = feedUrl
				dataSourceService.createOrUpdateDataSource(feed)
			}
		} else {
			flash.message = "GET response[${requestMap.get('responsecode')}]: ${requestMap.get('responsemessage')}"
			render(contentType: "application/vnd.google-earth.kml+xml", view:"kml")
			return
		}
		
		render(contentType: "application/vnd.google-earth.kml+xml", view:"kml", model:[feedInstance: feed, feedColor: color])
	}
	
	def convert = {
		
		if (request.method =="GET") {
				response.sendError 405, "Invalid HTTP method request ($request.method}): /convert accepts POST containing Atom GeoRSS content"
				return
		}		
				
		def feedContent = params.content
		String color = params.color;
		if (!color) {
			color = grailsApplication.config.rage.default.marker.color
			log.debug("Parameter 'color' not provided, setting to default: " + color);
		}
		Feed feed = new Feed()
		feed = feedParserService.parseFeed(feedContent, null)
		log.debug("Converting ${feed.title} received via POST from ${request.remoteHost}")
		
		Map<String, List<Placemark>> placemarks = feed.placemarks
		if (placemarks?.size() > 0) { 
			render(contentType: "application/vnd.google-earth.kml+xml", view:"kml", model:[feedInstance: feed, feedColor: color])
		} else {
			log.error "Invalid content (${request.getContentType()} received from ${request.getRemoteAddr()}" 
			response.sendError(415, "Error processing feed content: ${feed.title}. POST parameter 'content' did not contain a valid GeoRSS feed.")
			return
		}
	}
}
