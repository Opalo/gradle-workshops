package org.warsjawa.gradleWS.internal.quartz

import com.google.inject.Inject
import org.apache.commons.configuration.Configuration
import org.quartz.*

import static org.quartz.JobBuilder.newJob
import static org.quartz.TriggerBuilder.newTrigger
import static org.slf4j.LoggerFactory.getLogger

public class QuartzScheduler {

    private log = getLogger(getClass())

    private final Scheduler scheduler
    private final Configuration configuration

    private List<TriggerKey> triggers
    private List<JobKey> jobs

    @Inject
    public QuartzScheduler(final SchedulerFactory factory, final QuartzJobFactory jobFactory, final Configuration configuration) {
        this.configuration = configuration
        this.scheduler = factory.getScheduler()
        scheduler.setJobFactory(jobFactory)
        triggers = new LinkedList<TriggerKey>()
        jobs = new LinkedList<JobKey>()
    }

    public void start() {
        scheduler.start()
        scheduleJobs()
    }

    private void scheduleJobs() {
        List<Object> jobs = configuration.getList('int.quartz.jobs')
        for (Object o : jobs) {
            String[] jobDetails = ((String) o).split(":")
            log.info("Job to schedule {}:{}", jobDetails[0], jobDetails[1])
            try {
                schedule((Class<Job>) Class.forName(jobDetails[0]), jobDetails[1])
            } catch (Exception e) {
                log.error('Error while scheduling jobs in quartz', e)
            }
        }
    }

    private void schedule(Class<? extends Job> cls, String cron) throws SchedulerException {
        def jd = createJobDetail(cls)
        def t = createCronTrigger(jd, cron)
        scheduler.scheduleJob(jd, t)
        jobs.add(jd.getKey())
        triggers.add(t.getKey())
    }

    private JobDetail createJobDetail(Class<? extends Job> jobCls) {
        newJob(jobCls).
                withIdentity(jobCls.getName() + "-job-id", jobCls.getName() + "-job-group").
                requestRecovery(true).
                build()
    }

    private Trigger createCronTrigger(JobDetail jd, String cron) {
        newTrigger().
                withIdentity(jd.getJobClass().getName() + "trigger-id", jd.getJobClass().getName() + "trigger-group").
                withSchedule(CronScheduleBuilder.cronSchedule(cron)).
                forJob(jd).
                build()
    }

    public void unscheduleAll() throws SchedulerException {
        scheduler.unscheduleJobs(triggers)
        triggers = null
        scheduler.deleteJobs(jobs)
        jobs = null
    }

    public void shutdown() throws SchedulerException {
        unscheduleAll()
        scheduler.shutdown()
    }
}
