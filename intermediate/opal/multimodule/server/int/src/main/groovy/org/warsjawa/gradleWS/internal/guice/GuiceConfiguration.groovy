package org.warsjawa.gradleWS.internal.guice

import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.google.inject.servlet.GuiceServletContextListener
import org.warsjawa.gradleWS.commons.guice.RESTModule
import org.warsjawa.gradleWS.commons.store.CreditApplicationStore
import org.warsjawa.gradleWS.internal.guice.modules.DBModule
import org.warsjawa.gradleWS.internal.guice.modules.QuartzModule
import org.warsjawa.gradleWS.internal.quartz.QuartzScheduler
import org.warsjawa.gradleWS.internal.store.DBCreditApplicationStore

import javax.inject.Inject
import javax.servlet.ServletContextEvent

import static com.google.inject.Guice.createInjector
import static org.slf4j.LoggerFactory.getLogger

//curl -v -H "Accept: application\/json" -H "Content-type: application\/json" -X POST -d '{ "id": 123}'  http:\/\/localhost:8080\/int\/rest\/credit\/save
class GuiceConfiguration extends GuiceServletContextListener {

    private log = getLogger(getClass())
    @Inject
    private QuartzScheduler quartzScheduler


    @Override
    void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent)
        log.info('int started')
        initQuartz()
    }

    private void initQuartz() {
        quartzScheduler.start()
    }

    @Override
    void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent)
        log.info('int shutdown')
        quartzScheduler.shutdown()
    }

    private void destroyQuartz() {
        quartzScheduler.shutdown()
    }


    @Override
    protected Injector getInjector() {
        def injector = createInjector(
                new DBModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(CreditApplicationStore).to(DBCreditApplicationStore)
                    }
                },
                new RESTModule(),
                new QuartzModule(),
        )
        injector.injectMembers(this)
        injector
    }
}
