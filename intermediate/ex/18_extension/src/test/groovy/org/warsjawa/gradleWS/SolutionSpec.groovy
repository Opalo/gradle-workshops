package org.warsjawa.gradleWS

import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.testfixtures.ProjectBuilder.builder

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')

    def 'extension is added and dependencies configured'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.extensions.findByName('googleDependencies').getClass().superclass.simpleName == 'GoogleDependencies'

        and:
        project.configurations.findByName('compile').dependencies.collect {
            "$it.group:$it.name:$it.version".toString()
        }.containsAll('com.google.inject:guice:1.0', 'com.google.guava:guava:11.0')
    }
}
