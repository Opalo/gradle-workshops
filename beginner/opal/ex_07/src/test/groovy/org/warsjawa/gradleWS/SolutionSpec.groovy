package org.warsjawa.gradleWS

import org.gradle.api.GradleException
import org.gradle.tooling.ProjectConnection
import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.testfixtures.ProjectBuilder.builder
import static org.gradle.tooling.GradleConnector.newConnector

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')

    def 'external script is applied'() {
        given:
        def project = builder().withProjectDir(projectDir).build()
        def pluginsDir = new File('plugins')
        def infoPlugin = new File(pluginsDir,'info-plugin.gradle')

        expect:
        pluginsDir.exists()
        pluginsDir.isDirectory()

        infoPlugin.exists()
        infoPlugin.isFile()

        when:
        project.evaluate()

        then:
        project.tasks.findByName('info')
    }
}
