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

    def 'appropriate rule is added'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.tasks.rules.find { it.description == 'Pattern: ping<ID>, where <ID> is one of [DEV, TST, UAT, PROD]' }
    }

    def 'all ping* tasks are added'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.tasks['pingDEV']
        project.tasks['pingTST']
        project.tasks['pingUAT']
        project.tasks['pingPROD']
        project.tasks['pingAll']
    }

    def 'pingAll task generates appropriate output'() {
        given:
        def connection = newConnector().forProjectDirectory(projectDir).connect()

        when:
        def output = runGradle(connection, 'pingAll').toString('UTF-8').split('\n') as List

        then:
        output.containsAll(['DEV', 'UAT', 'TST', 'PROD'].collect { "Pinging: $it".toString() })

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
