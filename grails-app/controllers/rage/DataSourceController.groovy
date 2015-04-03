package rage

import org.springframework.dao.DataIntegrityViolationException

class DataSourceController {

	def grailsApplication
	
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		if (true != grailsApplication.config.rage.discovery.enable) {
			redirect(uri:'/')
            return
		}
        [dataSourceInstanceList: DataSource.list(params), dataSourceInstanceTotal: DataSource.count()]
    }

	/*
    def create() {
        [dataSourceInstance: new DataSource(params)]
    }

    def save() {
        def dataSourceInstance = new DataSource(params)
        if (!dataSourceInstance.save(flush: true)) {
            render(view: "create", model: [dataSourceInstance: dataSourceInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'dataSource.label', default: 'DataSource'), dataSourceInstance.id])
        redirect(action: "show", id: dataSourceInstance.id)
    }

    def show(Long id) {
        def dataSourceInstance = DataSource.get(id)
        if (!dataSourceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'dataSource.label', default: 'DataSource'), id])
            redirect(action: "list")
            return
        }

        [dataSourceInstance: dataSourceInstance]
    }

    def edit(Long id) {
        def dataSourceInstance = DataSource.get(id)
        if (!dataSourceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'dataSource.label', default: 'DataSource'), id])
            redirect(action: "list")
            return
        }

        [dataSourceInstance: dataSourceInstance]
    }

    def update(Long id, Long version) {
        def dataSourceInstance = DataSource.get(id)
        if (!dataSourceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'dataSource.label', default: 'DataSource'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (dataSourceInstance.version > version) {
                dataSourceInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'dataSource.label', default: 'DataSource')] as Object[],
                          "Another user has updated this DataSource while you were editing")
                render(view: "edit", model: [dataSourceInstance: dataSourceInstance])
                return
            }
        }

        dataSourceInstance.properties = params

        if (!dataSourceInstance.save(flush: true)) {
            render(view: "edit", model: [dataSourceInstance: dataSourceInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'dataSource.label', default: 'DataSource'), dataSourceInstance.id])
        redirect(action: "show", id: dataSourceInstance.id)
    }

    def delete(Long id) {
        def dataSourceInstance = DataSource.get(id)
        if (!dataSourceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'dataSource.label', default: 'DataSource'), id])
            redirect(action: "list")
            return
        }

        try {
            dataSourceInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'dataSource.label', default: 'DataSource'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'dataSource.label', default: 'DataSource'), id])
            redirect(action: "show", id: id)
        }
    }
    */
}
