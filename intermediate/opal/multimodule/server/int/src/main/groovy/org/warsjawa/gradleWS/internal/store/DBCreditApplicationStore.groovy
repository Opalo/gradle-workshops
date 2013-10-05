package org.warsjawa.gradleWS.internal.store

import org.apache.commons.configuration.Configuration
import org.mapdb.DB
import org.warsjawa.gradleWS.commons.store.CreditApplicationStore
import org.warsjawa.gradleWS.commons.vo.CreditApplication

import javax.inject.Inject

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull
import static java.lang.System.nanoTime
import static org.slf4j.LoggerFactory.getLogger

class DBCreditApplicationStore implements CreditApplicationStore {

    private log = getLogger(getClass())

    @Inject DB db
    @Inject Configuration conf

    @Override
    Long saveCreditApplication(CreditApplication ca) {
        checkNotNull(ca, 'Null application passed')
        def id = nanoTime()
        ca.id = id
        log.info('Saving CA: {}', ca)
        mapDB.put(id, ca)
        db.commit()
        id
    }

    void saveCreditApplicationWithID(CreditApplication ca) {
        checkNotNull(ca, 'Null application passed')
        checkNotNull(ca.id, 'Null id passed')
        checkArgument(mapDB.get(ca.id) == null, "Application with id: $ca.id already exists")
        log.info('Saving CA: {}', ca)
        mapDB.put(ca.id, ca)
        db.commit()
    }

    @Override
    CreditApplication creditApplicationForId(Long id) {
        checkNotNull(id, 'Null id passed')
        def ca = mapDB.get(id)
        ca ?: new CreditApplication()
    }

    @Override
    List<CreditApplication> list() {
        mapDB.collect { mapDB.get(it.key) }
    }

    @Override
    void delete(Long id) {
        throw new UnsupportedOperationException()
    }

    @Lazy
    private Map<Long, CreditApplication> mapDB = {
        db.getTreeMap(conf.getString('int.store.name'))
    }()
}
