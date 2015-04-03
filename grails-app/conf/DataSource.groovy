environments {
    development {
        //grails.mongo properties loaded from Config.groovy
    }
    test {
		grails {
			mongo {
				host = "localhost"
				port = 27017
				databaseName = "rage-test"
			}
		}
    }
    production {
        //grails.mongo properties loaded from classpath:RAGE-config.groovy
    }
}
