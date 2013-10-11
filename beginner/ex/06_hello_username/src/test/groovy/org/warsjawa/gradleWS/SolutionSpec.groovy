package org.warsjawa.gradleWS

import org.gradle.tooling.ProjectConnection
import spock.lang.Specification

import static org.gradle.tooling.GradleConnector.newConnector

class SolutionSpec extends Specification {

    def 'project has valid output'() {
        given:
        def projectDir = new File('.')
        def projectConnection = newConnector().forProjectDirectory(projectDir).connect()

        when:
        def output = runGradle(projectConnection, 'hello').toString('UTF-8').split('\n')

        then:
        output.find { it == "Hello ${System.properties['user.name']}" }

        cleanup:
        projectConnection.close()
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).run()
        output
    }
}
