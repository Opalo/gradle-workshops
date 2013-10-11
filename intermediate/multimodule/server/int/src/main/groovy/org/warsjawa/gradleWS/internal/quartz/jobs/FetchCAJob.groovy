package org.warsjawa.gradleWS.internal.quartz.jobs

import groovy.json.JsonSlurper
import org.apache.commons.configuration.Configuration
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.warsjawa.gradleWS.commons.vo.CreditApplication
import org.warsjawa.gradleWS.internal.store.DBCreditApplicationStore

import javax.inject.Inject

import static org.apache.http.impl.client.HttpClients.createDefault
import static org.apache.http.util.EntityUtils.consumeQuietly
import static org.slf4j.LoggerFactory.getLogger

@DisallowConcurrentExecution
public class FetchCAJob implements Job {

    private log = getLogger(getClass())

    @Inject Configuration conf
    @Inject DBCreditApplicationStore store

    @Override
    public void execute(JobExecutionContext context) {

        def httpClient = createDefault()
        def response = null

        try {
            def request = new HttpGet(listHost)
            response = httpClient.execute(request)
            log.info('Status line: {}', response.statusLine)
            def content = response.entity.content.text
            log.info('Fetched CA: {}', content)

            def json = new JsonSlurper().parseText(content)
            def list = json.collect { new CreditApplication(it as Map) }
            list.each {
                try {
                    store.saveCreditApplicationWithID(it)
                } catch (IllegalArgumentException e) {}
            }

            consumeQuietly(response.entity)

            list.each {
                request = new HttpDelete("$delHost$it.id")
                response = httpClient.execute(request)
                log.info('Status line: {}', response.statusLine)
                consumeQuietly(response.entity)
            }

        } catch (Exception e) {
            log.error('Error while fetching applications from host: {}, msg: {}', listHost, e.message)
        } finally {
            response?.close()
            httpClient?.close()
        }
    }

    @Lazy
    private String listHost = {
        conf.getString('int.ext.rest.list')
    }()

    @Lazy
    private String delHost = {
        conf.getString('int.ext.rest.del')
    }()
}
