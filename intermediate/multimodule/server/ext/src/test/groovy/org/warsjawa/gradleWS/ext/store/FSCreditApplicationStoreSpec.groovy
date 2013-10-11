package org.warsjawa.gradleWS.ext.store

import org.apache.commons.configuration.Configuration
import org.warsjawa.gradleWS.commons.vo.CreditApplication
import spock.lang.Specification

import static com.google.common.io.Files.createTempDir
import static groovy.json.JsonOutput.toJson

class FSCreditApplicationStoreSpec extends Specification {

    def fsStore = createTempDir()
    def store = new FSCreditApplicationStore(conf: GroovyStub(Configuration) {
        getString('ext.store.path') >> fsStore.absolutePath
    })

    def cleanup() {
        fsStore.delete()
    }

    def 'app is saved'() {
        given:
        def ca = new CreditApplication()

        when:
        def id = store.saveCreditApplication(ca)

        then:
        id
        id.toString().matches('\\d+')

        then:
        new File(fsStore, id.toString()).text == toJson(ca)
    }

    def 'exception when null app passed for saving'() {
        when:
        store.saveCreditApplication(null)

        then:
        def e = thrown(NullPointerException)
        e.message == 'Null application passed'
    }

    def 'app for id is returned'() {
        given:
        def ca = new CreditApplication()
        def id = store.saveCreditApplication(ca)

        when:
        def ca2 = store.creditApplicationForId(id)

        then:
        ca2.id == ca.id
    }

    def 'exception when null id passed for fetch'() {
        when:
        store.creditApplicationForId(null)

        then:
        def e = thrown(NullPointerException)
        e.message == 'Null id passed'
    }

    def 'list of applications is returned'() {
        given:
        def apps = [new CreditApplication(), new CreditApplication()]

        and:
        apps.each { store.saveCreditApplication(it) }

        when:
        def list = store.list()

        then:
        list.size() == 2
        list.id.sort() == apps.id.sort()
    }

    def 'app is deleted'() {
        given:
        def id = store.saveCreditApplication(new CreditApplication())

        expect:
        def f = new File(fsStore, id.toString())
        f.exists()
        f.size() > 0

        when:
        store.delete(id)

        then:
        !f.exists()

    }

    def 'exception when null id passed for deletion'() {
        when:
        store.delete(null)

        then:
        def e = thrown(NullPointerException)
        e.message == 'Null id passed'
    }
}
