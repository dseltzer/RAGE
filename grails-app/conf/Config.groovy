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
// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts
grails.config.locations = ["classpath:RAGE-config.groovy"];

grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        //In production, rage properties are loaded from external configuration file 'RAGE-config.groovy' located on the classpath

		def catalinaBase = System.properties.getProperty('catalina.base')
		if (!catalinaBase) catalinaBase = "."
		def logDirectory = "${catalinaBase}/logs"
		log4j = {
			appenders {
				rollingFile name:'tomcatLog', maxFileSize:1024, file:"${logDirectory}/RAGE.log"
			}
			root {
				error 'tomcatLog'
				additivity = true
			}

			info 'grails.app'
		} 
    }
    development {
		grails.serverURL = "http://localhost:8080/${appName}"
		rage.server.url = "localhost:8080/${appName}"
		rage.default.marker.color = "ff0000"
	
		//new params for RAGE 2.0
		rage.map.enable = true
		rage.openlayrs.map.xyz.url = "http://otile1.mqcdn.com/tiles/1.0.0/osm/\${z}/\${x}/\${y}.jpg"  //mapquest - more at http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
		rage.openlayrs.map.xyz.name = "mapquest"
		rage.discovery.enable = true
		grails.mongo.host = "127.0.0.1"
		grails.mongo.port = 27017
		//grails.mongo.username = admin
		//grails.mongo.password = Changeit123
		grails.mongo.databaseName = "rage-dev"
	
		log4j = {
			debug 'grails-app'
		}	

    }
    test {
      grails.serverURL = "http://localhost:8080/${appName}"
	  log4j = {
		  debug 'grails-app'
	  }
    }

}
