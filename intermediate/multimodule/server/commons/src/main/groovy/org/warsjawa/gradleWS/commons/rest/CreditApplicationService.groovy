package org.warsjawa.gradleWS.commons.rest

import org.warsjawa.gradleWS.commons.store.CreditApplicationStore
import org.warsjawa.gradleWS.commons.vo.CreditApplication

import javax.inject.Inject
import javax.ws.rs.*

import static javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path('credit')
class CreditApplicationService {

    @Inject CreditApplicationStore store

    @POST
    @Path('save')
    @Produces(APPLICATION_JSON)
    CreditApplication save(CreditApplication application) {
        def id = store.saveCreditApplication(application)
        application.id = id
        application
    }

    @GET
    @Path('app/{id}')
    @Produces(APPLICATION_JSON)
    CreditApplication CAForId(@PathParam('id') Long id) {
        store.creditApplicationForId(id)
    }

    @GET
    @Path('list')
    @Produces(APPLICATION_JSON)
    List<CreditApplication> list() {
        store.list()
    }

    @DELETE
    @Path('del/{id}')
    void deleteCAForIDs(@PathParam('id') Long id) {
        store.delete(id)
    }

    @GET
    @Path('hi')
    @Produces(APPLICATION_JSON)
    String hi() {
        'Hi!'
    }
}
