package org.warsjawa.gradleWS.ext.store

import com.google.common.base.Preconditions
import groovy.json.JsonSlurper
import org.apache.commons.configuration.Configuration
import org.warsjawa.gradleWS.commons.store.CreditApplicationStore
import org.warsjawa.gradleWS.commons.vo.CreditApplication

import javax.inject.Inject

import static groovy.json.JsonOutput.toJson
import static java.lang.System.nanoTime
import static org.slf4j.LoggerFactory.getLogger

class FSCreditApplicationStore implements CreditApplicationStore {

    private log = getLogger(getClass())

    @Inject Configuration conf

    @Override
    Long saveCreditApplication(CreditApplication ca) {
        Preconditions.checkNotNull(ca, 'Null application passed')
        def id = nanoTime()
        ca.id = id
        def caFile = new File(store, id.toString())
        log.info('Saving CA: {}', ca)
        caFile.text = toJson(ca)
        id
    }

    @Override
    CreditApplication creditApplicationForId(Long id) {
        Preconditions.checkNotNull(id, 'Null id passed')
        def ca = store.listFiles().find { it.name == id.toString() }
        ca ? new CreditApplication(new JsonSlurper().parseText(ca.text) as Map) : new CreditApplication()
    }

    @Override
    List<CreditApplication> list() {
        store.listFiles().sort { it.name }.collect {
            new CreditApplication(new JsonSlurper().parseText(it.text) as Map)
        }
    }

    @Override
    void delete(Long id) {
        Preconditions.checkNotNull(id, 'Null id passed')
        log.info('Searching for CA with id: {}', id)
        def ca = store.listFiles().find { it.name == id.toString() }
        log.info('Found CA: {}', ca?.absolutePath)
        ca?.delete()
    }

    @Lazy
    private File store = {
        def f = new File(conf.getString('ext.store.path'))
        f.mkdirs()
        f
    }()
}
