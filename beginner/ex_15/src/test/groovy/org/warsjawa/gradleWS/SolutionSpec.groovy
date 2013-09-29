package org.warsjawa.gradleWS

import org.gradle.tooling.ProjectConnection
import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.testfixtures.ProjectBuilder.builder
import static org.gradle.tooling.GradleConnector.newConnector

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')
    @Shared projectConnection = newConnector().forProjectDirectory(projectDir).connect()

    def cleanupSpec() {
        projectConnection.close()
    }

    def 'test task is run and appropriate output is generated'() {
        given:
        def project = builder().withProjectDir(projectDir).build()
        def connection = newConnector().forProjectDirectory(projectDir).connect()

        when:
        def output = runGradle(connection, 'test').toString('UTF-8').split('\n')

        then:
        output.size() == 1
        output[0] == 'org.warsjawa.gradleWS.ExSpec test runs longer than 20ms'

        cleanup:
        connection.close()
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).withArguments('-q', '-Dtest.single=ExSpec').run()
        output
    }
}
