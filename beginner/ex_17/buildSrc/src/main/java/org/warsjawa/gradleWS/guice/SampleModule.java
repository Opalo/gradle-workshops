package org.warsjawa.gradleWS.guice;

import com.google.inject.AbstractModule;

public class SampleModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Printer.class).to(StdPrinter.class);
    }
}
