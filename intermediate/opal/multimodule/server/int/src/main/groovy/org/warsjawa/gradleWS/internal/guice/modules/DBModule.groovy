package org.warsjawa.gradleWS.internal.guice.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import org.apache.commons.configuration.Configuration
import org.apache.commons.configuration.PropertiesConfiguration
import org.mapdb.DB
import org.mapdb.StoreDirect
import org.mapdb.StoreWAL

import static org.mapdb.DBMaker.newFileDB

class DBModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    private DB db() {
        def dbFile = new File(configuration().getString('int.store.path'))
        new File(dbFile.getPath() + StoreDirect.DATA_FILE_EXT).createNewFile()
        new File(dbFile.getPath() + StoreWAL.TRANS_LOG_FILE_EXT).createNewFile()
        newFileDB(dbFile)
                .closeOnJvmShutdown()
                .encryptionEnable(configuration().getString('int.store.pass'))
                .make()
    }

    @Provides
    @Singleton
    private Configuration configuration() {
        new PropertiesConfiguration(getClass().getClassLoader().getResource('int.properties').toURI().toURL())
    }
}
