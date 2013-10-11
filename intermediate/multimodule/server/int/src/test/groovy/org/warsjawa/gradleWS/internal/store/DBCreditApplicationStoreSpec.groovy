package org.warsjawa.gradleWS.internal.store

import org.apache.commons.configuration.Configuration
import org.warsjawa.gradleWS.commons.vo.CreditApplication
import spock.lang.Specification

import static org.mapdb.DBMaker.newFileDB

class DBCreditApplicationStoreSpec extends Specification {

    def dbName = 'test'
    def dbStore = File.createTempFile('test', 'db')
    def store = new DBCreditApplicationStore(
            conf: GroovyStub(Configuration) {
                getString('int.store.name') >> dbName
            },
            db: db()
    )

    def db() {
        newFileDB(dbStore)
                .closeOnJvmShutdown()
                .make()
    }

    def cleanup() {
        dbStore.delete()
    }

    def 'application is saved'() {
        given:
        def ca = new CreditApplication(firstName: 'John', lastName: 'Lol')

        when:
        def id = store.saveCreditApplication(ca)

        then:
        id
        id.toString().matches('\\d+')

        then:
        def caFromDb = store.db.getTreeMap(dbName).get(id)
        caFromDb == ca
    }

    def 'exception when null app passed for saving'() {
        when:
        store.saveCreditApplication(null)

        then:
        def e = thrown(NullPointerException)
        e.message == 'Null application passed'
    }

    def 'application for id is returned'() {
        given:
        def ca = new CreditApplication()
        def id = store.saveCreditApplication(ca)

        when:
        def ca2 = store.creditApplicationForId(id)

        then:
        ca2.id == ca.id
    }

    def 'exception when null id passed'() {
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

    def 'delete is not supported'() {
        when:
        store.delete(null)

        then:
        thrown(UnsupportedOperationException)
    }

    def 'application with id is saved'() {
        given:
        def ca = new CreditApplication(id: 1L, firstName: 'John', lastName: 'Lol')

        when:
        store.saveCreditApplicationWithID(ca)

        then:
        ca.id == 1L

        then:
        def caFromDb = store.db.getTreeMap(dbName).get(ca.id)
        caFromDb == ca
    }

    def 'exception when null application with id is saved'() {
        when:
        store.saveCreditApplicationWithID(null)

        then:
        def e = thrown(NullPointerException)
        e.message == 'Null application passed'
    }

    def 'exception when application with empty id is saved'() {
        when:
        store.saveCreditApplicationWithID(new CreditApplication())

        then:
        def e = thrown(NullPointerException)
        e.message == 'Null id passed'
    }

    def 'exception when application with id already exists'() {
        given:
        def ca = new CreditApplication(id: 1L, firstName: 'John', lastName: 'Lol')

        and:
        store.saveCreditApplicationWithID(ca)

        when:
        store.saveCreditApplicationWithID(new CreditApplication(id: 1L))

        then:
        def e = thrown(IllegalArgumentException)
        e.message == 'Application with id: 1 already exists'
    }
}
