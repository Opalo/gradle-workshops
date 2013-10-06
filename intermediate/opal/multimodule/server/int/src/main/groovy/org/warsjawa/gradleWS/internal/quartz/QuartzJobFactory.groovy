package org.warsjawa.gradleWS.internal.quartz

import com.google.inject.Inject
import com.google.inject.Injector
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle

public class QuartzJobFactory implements JobFactory {

    @Inject
    private Injector injector;

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        def jobDetail = bundle.getJobDetail()
        def jobClass = jobDetail.getJobClass()
        injector.getInstance(jobClass)
    }
}
