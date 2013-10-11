package org.warsjawa.gradleWS.commons.guice

import com.sun.jersey.guice.JerseyServletModule
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer

class RESTModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
        configureRestServlet()
    }

    private void configureRestServlet() {
        serve('/rest/*').with(GuiceContainer, restServletParams)
    }

    @Lazy
    private Map<String, String> restServletParams = {
        [
                'com.sun.jersey.config.property.packages': 'org.warsjawa.gradleWS',
                'com.sun.jersey.api.json.POJOMappingFeature': 'true'
        ]
    }()
}
