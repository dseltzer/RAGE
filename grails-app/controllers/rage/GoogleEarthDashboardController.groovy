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
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

import rage.filters.Filter
import rage.filters.BoundingBoxGeoFilter
import rage.filters.NumericalFilter
import rage.filters.StringFilter
import rage.utils.HttpUtils

/**
 * Controller to allow users to create Google Earth - RAGE dashboard KML
 * file featuring syndication feeds of interest
 */
class GoogleEarthDashboardController {

	def feedParserService;

	def index = { redirect(action:create) }

	def create = { }
	
	def timemap = {
		List<String> feedUrls = [];
		List<String> feedColors = [];
		int i = 1;
		while (true) {
			if (params."${'feedUrl' + i}") {
				feedUrls << params."${'feedUrl' + i}"
				String color = "ff0000"
				if (params."${'feedColor' + i}") {
					color = stripPound(params."${'feedColor' + i}")
				}
				feedColors << color
			} else {
				//Not more input rows
				break;
			}
			i++;
		}
		
		String view = "timemap"
		if (feedUrls.size() == 0) view = "create"
		render(view:view, model:[feedUrls: feedUrls , feedColors: feedColors])
	}

	def kml = {
		def folderName = 'rage';
		def feeds = [];
		def feedColors = [];
		def feedFilters = [];
		int i = 1;

		if (params.folderName) {
			folderName = params.folderName;
		}

		/*
		 * Build model objects from variable number of input rows
		 */
		while (true) {
			if (params."${'feedUrl' + i}") {
				//TODO: add domain lookup by url before attempting GET

				//Get feed to flesh out feed object (or fill feed details with error msgs) 
				Feed feed = null;
				Map requestMap = HttpUtils.getFeed(params."${'feedUrl' + i}", null,  null);
				if (200 == requestMap.get('responsecode')) {			
					try {
						feed = feedParserService.parseFeed(requestMap.get('content'), null);
						feed.url = params."${'feedUrl' + i}";
					} catch (Exception e) {
						feed = new Feed();
						feed.url = params."${'feedUrl' + i}";
						feed.title = params."${'feedUrl' + i}";
						feed.description = "Parse Exception: " + e.getMessage();
					}
				} else {
					feed = new Feed();
					feed.url = params."${'feedUrl' + i}";
					feed.title = params."${'feedUrl' + i}";
					feed.description = "Unable to GET feed[${requestMap.get('responsecode')}]: ${requestMap.get('responsemessage')}";
				}
				feeds << feed;

				//Strip '#' if contained in color value
				String color = "ff0000"
				if (params."${'feedColor' + i}") {
					color = stripPound(params."${'feedColor' + i}")
				}
				feedColors << color;
				
				List<String> filters = []
				if (params."${'titleFilterCheckbox' + i}") {
					Filter f = StringFilter.instanceOfTitleFilter(params."${'titleOperator' + i}", params."${'titleFilter' + i}")
					filters << f.serializeToUrlParam()
				}
				if (params."${'contentFilterCheckbox' + i}") {
					Filter f = StringFilter.instanceOfContentFilter(params."${'contentOperator' + i}", params."${'contentFilter' + i}")
					filters << f.serializeToUrlParam()
				}
				if (params."${'numericalFilterCheckbox' + i}") {
					Filter f = NumericalFilter.instanceOf(params."${'numericalField' + i}", params."${'numericalFilterVariable' + i}", params."${'numericalOperator' + i}", new Double(params."${'numericalFilterOperand' + i}"))
					filters << f.serializeToUrlParam()
				}
				if (params."${'boundingBoxFilterCheckbox' + i}") {
					Filter f = BoundingBoxGeoFilter.instanceOfDecimalDegrees(new Double(params."${'upperLeftLat' + i}"), new Double(params."${'upperLeftLon' + i}"), new Double(params."${'lowerRightLat' + i}"), new Double(params."${'lowerRightLon' + i}"))
					filters << f.serializeToUrlParam()
				}
				feedFilters << filters
			} else {
				//Not more input rows
				break;
			}
			i++;
		}

		response.setHeader("Content-disposition", "attachment; filename=${folderName}.kml");
		render(contentType: "application/vnd.google-earth.kml+xml; charset=UTF-8", 
			view:"kml", 
			model:[folderName: folderName, feeds: feeds, feedColors: feedColors, feedFilters: feedFilters]);
	}
	
	private String stripPound(String s) {
		int poundIndex = s.indexOf('#')
		if (poundIndex == 0) {
			return s.substring(1)
		} else {
			return s
		}
		
	}
}
