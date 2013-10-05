package org.warsjawa.gradleWS.ext.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provider
import com.google.inject.servlet.GuiceServletContextListener
import org.apache.commons.configuration.Configuration
import org.apache.commons.configuration.PropertiesConfiguration
import org.warsjawa.gradleWS.commons.guice.RESTModule
import org.warsjawa.gradleWS.commons.store.CreditApplicationStore
import org.warsjawa.gradleWS.ext.store.FSCreditApplicationStore

import javax.servlet.ServletContextEvent

import static com.google.inject.Scopes.SINGLETON
import static org.slf4j.LoggerFactory.getLogger

class GuiceConfiguration extends GuiceServletContextListener {

    private log = getLogger(getClass())

    @Override
    void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent)
        log.info('ext started')
    }

    @Override
    void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent)
        log.info('ext shutdown')
    }

    @Override
    protected Injector getInjector() {
        Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Configuration).toProvider(new Provider<Configuration>() {
                            @Override
                            Configuration get() {
                                new PropertiesConfiguration(getClass().getClassLoader().getResource('ext.properties').toURI().toURL())
                            }
                        }).in(SINGLETON)
                        bind(CreditApplicationStore).to(FSCreditApplicationStore)
                    }
                },
                new RESTModule()
        )
    }
}
