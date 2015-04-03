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

import groovy.util.slurpersupport.GPathResult
import rage.Feed.FeedType
import rage.GeoParserService.GeoType
import rage.utils.DateParser
import rage.filters.Filter
import java.awt.geom.Point2D

class FeedParserService {

	public static final String PARSE_ERROR_TITLE = "Feed parse error";
	public static final String PARSE_ERROR_NO_TYPE_MSG = "Unexpected feed type, is this an RSS or Atom feed?";
	public static final String PARSE_ERROR_MALFORMED_XML_MSG = "Unable to parse feed, malformed XML";

	def geoParserService

	public Feed parseFeed(String feedContent, List<Filter> filters) {
		Feed feed = new Feed()
		Map<String, List<Placemark>> placemarks = [:]
		
		GeoType type = geoParserService.getGeoType(feedContent)
		def feedXml;
		try {
			feedXml = new XmlSlurper().parseText(feedContent);
		} catch (Exception e) {
			feed.title = PARSE_ERROR_TITLE;
			feed.description = PARSE_ERROR_MALFORMED_XML_MSG;
			return feed;
		}

		if (feedXml.name().toLowerCase().trim().equals('feed')) {
			feed.type = FeedType.ATOM
			feed.title = feedXml.title.text()
			feed.description = feedXml.subtitle.text()
			if (feedXml.entry.size() >= 1) {
				feedXml.entry.each{
					addPlacemark(parseAtomEntry(type, it), placemarks, filters)
				}
			}
		} else if (feedXml.name().toLowerCase().trim().equals('rdf')) {
			feed.type = FeedType.RSS_1_0
			feed.title = feedXml.channel.title.text()
			feed.description = feedXml.channel.description.text()
			if (feedXml.item.size() >= 1) {
				feedXml.item.each{
					addPlacemark(parseRssEntry(type, it), placemarks, filters)
				}
			}
		} else if (feedXml.name().toLowerCase().trim().equals('rss')) {
			feed.type = FeedType.RSS_2_0
			feed.title = feedXml.channel.title.text()
			feed.description = feedXml.channel.description.text()
			if (feedXml.channel.item.size() >= 1) {
				feedXml.channel.item.each{
					addPlacemark(parseRssEntry(type, it), placemarks, filters)
				}
			}
		} else {
			feed.title = PARSE_ERROR_TITLE;
			feed.description = PARSE_ERROR_NO_TYPE_MSG;
		}
		feed.placemarks = placemarks

		return feed
	}

	private void addPlacemark(Placemark p, Map<String, List<Placemark>> map, List<Filter> filters) {
		if (null != filters) {
			Iterator<Filter> itr = filters.iterator()
			while (itr.hasNext()) {
				Filter filter = itr.next()
				if (!filter.accept(p)) {
					//Filter rejected placemark, drop
					return
				}
			}	
		}
		
		String key = p.name
		List<Placemark> placemarkList = null
		if (map.containsKey(key)) {
			placemarkList = map.get(key)
		} else {
			placemarkList = []
		}
		
		placemarkList << p
		map.put(key, placemarkList)
	}
	
	private Placemark parseAtomEntry(GeoType type, GPathResult node) {
		def placemark = new Placemark()
		placemark.name = node.title.text()
		if (node.content.size() == 1) {
			String desc = ""
			if ("application/xml".equals(node.content.@type.text())) {
				desc = convertXmlToHtml("", node.content)
			} else {
				desc = removeCDATAWrapper(node.content.text())
			}
			placemark.description = desc
		} else if (node.summary.size() == 1) {
			String desc = node.summary.text()
			placemark.description = removeCDATAWrapper(desc)
		}

		if (node.published.size() == 1) {
			placemark.timestamp = node.published.text()
		} else if (node.updated.size() == 1) {
			placemark.timestamp = node.updated.text()
		}
		
		if (node.link.size() >= 1) {
			List<Enclosure> enclosures = []
			node.link.each{link->
				String linkRel = (String) link.@rel
				if (linkRel.equals("enclosure")) {
					String title = (String) link.@title
					if (!title) {
						title = (String) link.@href
					}
					Enclosure enclosure = new Enclosure(title: title, url: (String) link.@href, type: (String) link.@type)
					enclosures.add(enclosure)
				}
			}
			placemark.enclosures = enclosures
		}

		placemark.geometryList = geoParserService.parseGeometry(type, node)
		return placemark
	}

	private Placemark parseRssEntry(GeoType type, GPathResult node) {
		def placemark = new Placemark()
		placemark.name = node.title.text()
		
		if (node.encoded.size() == 1) {
			String desc = node.encoded.text()
			placemark.description =  removeCDATAWrapper(desc)
		} else if (node.description.size()) {
			String desc = node.description.text()
			placemark.description = removeCDATAWrapper(desc)
		}

		if (node.date.size() == 1) {
			placemark.timestamp = node.date.text()
		} else if (node.pubDate.size() == 1) {
			String pubDate = node.pubDate.text()
			placemark.timestamp = DateParser.convertToISO8601(pubDate)
		}
		
		List<Enclosure> enclosures = []
		if (node.link.size() == 1) {
			Enclosure enclosure = new Enclosure(title: "link", url: (String) node.link, type: "text/html")
			enclosures.add(enclosure)
		}
		if (node.enclosure.size() == 1) {
			//RSS feeds can only have one enclosure!
			Enclosure enclosure = new Enclosure(title: (String) node.enclosure.@url, url: (String) node.enclosure.@url, type: (String) node.enclosure.@type)
			enclosures.add(enclosure)
		}
		if (enclosures.size() > 0) {
			placemark.enclosures = enclosures
		}
		
		placemark.geometryList = geoParserService.parseGeometry(type, node)
		return placemark
	}

	private String removeCDATAWrapper(String value) {
		return value.replaceAll('<!\\[CDATA\\[', "").replaceAll('\\]\\]>', "")
	}
	
	private String convertXmlToHtml(String indent, GPathResult node) {
		StringBuffer buf = new StringBuffer()
		buf.append(indent)
		buf.append(node.name())
		buf.append(": ")
		
		def children = node.children()
		if (0 == children.size()) {
			buf.append(node)
			return buf.toString()
		}
		children.each {
			buf.append("<br />")
			buf.append(convertXmlToHtml(indent + "&nbsp;&nbsp;&nbsp;", it))
		}
		return buf.toString()
	}
}
