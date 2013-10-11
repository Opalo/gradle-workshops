package org.warsjawa.gradleWS.internal.guice.modules

import com.google.inject.AbstractModule
import org.quartz.SchedulerFactory
import org.quartz.impl.StdSchedulerFactory
import org.warsjawa.gradleWS.internal.quartz.QuartzJobFactory
import org.warsjawa.gradleWS.internal.quartz.QuartzScheduler

import static com.google.inject.Scopes.SINGLETON

public class QuartzModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SchedulerFactory).to(StdSchedulerFactory).in(SINGLETON)
        bind(QuartzJobFactory).in(SINGLETON)
        bind(QuartzScheduler).in(SINGLETON)
    }
}
