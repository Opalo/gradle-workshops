package org.warsjawa.gradleWS

import org.gradle.tooling.ProjectConnection
import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.tooling.GradleConnector.newConnector

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')
    @Shared projectConnection = newConnector().forProjectDirectory(projectDir).connect()

    def cleanupSpec() {
        projectConnection.close()
    }

    def 'sampleTask is run with appropriate output'() {
        given:
        def connection = newConnector().forProjectDirectory(projectDir).connect()

        when:
        def output = runGradle(connection, 'sampleTask').toString('UTF-8').split('\n') as List

        then:
        noExceptionThrown()
        output.size() == 3
        output[0] == 'Applying: org.warsjawa.gradleWS.plugins.SamplePlugin'
        output[1] == 'Running: org.warsjawa.gradleWS.tasks.SampleTask_Decorated'
        output[2] == 'This is StdPrinter'

        cleanup:
        connection.close()
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).withArguments('-q').run()
        output
    }
}
