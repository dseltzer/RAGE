package rage

import org.junit.*
import grails.test.mixin.*

@TestFor(DataSourceController)
@Mock(DataSource)
class DataSourceControllerTests {

    void testIndex() {
        controller.index()
        assert "/dataSource/list" == response.redirectedUrl
    }

    void testList() {
		config.rage.discovery.enable = true
        def model = controller.list()

        assert model.dataSourceInstanceList.size() == 0
        assert model.dataSourceInstanceTotal == 0
    }
	
	void testList_mongoDisabled() {
		config.rage.discovery.enable = false
		controller.list()
		assert response.redirectedUrl == '/'
	}

	/*
    void testCreate() {
        def model = controller.create()

        assert model.dataSourceInstance != null
    }

    void testSave() {
        controller.save()

        assert model.dataSourceInstance != null
        assert view == '/dataSource/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/dataSource/show/1'
        assert controller.flash.message != null
        assert DataSource.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/dataSource/list'

        populateValidParams(params)
        def dataSource = new DataSource(params)

        assert dataSource.save() != null

        params.id = dataSource.id

        def model = controller.show()

        assert model.dataSourceInstance == dataSource
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/dataSource/list'

        populateValidParams(params)
        def dataSource = new DataSource(params)

        assert dataSource.save() != null

        params.id = dataSource.id

        def model = controller.edit()

        assert model.dataSourceInstance == dataSource
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/dataSource/list'

        response.reset()

        populateValidParams(params)
        def dataSource = new DataSource(params)

        assert dataSource.save() != null

        // test invalid parameters in update
        params.id = dataSource.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/dataSource/edit"
        assert model.dataSourceInstance != null

        dataSource.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/dataSource/show/$dataSource.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        dataSource.clearErrors()

        populateValidParams(params)
        params.id = dataSource.id
        params.version = -1
        controller.update()

        assert view == "/dataSource/edit"
        assert model.dataSourceInstance != null
        assert model.dataSourceInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/dataSource/list'

        response.reset()

        populateValidParams(params)
        def dataSource = new DataSource(params)

        assert dataSource.save() != null
        assert DataSource.count() == 1

        params.id = dataSource.id

        controller.delete()

        assert DataSource.count() == 0
        assert DataSource.get(dataSource.id) == null
        assert response.redirectedUrl == '/dataSource/list'
    }*/
}
