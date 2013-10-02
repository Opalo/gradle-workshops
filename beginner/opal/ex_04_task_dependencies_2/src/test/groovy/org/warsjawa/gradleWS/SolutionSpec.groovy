package org.warsjawa.gradleWS

import org.gradle.tooling.ProjectConnection
import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.testfixtures.ProjectBuilder.builder
import static org.gradle.tooling.GradleConnector.newConnector

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')

    def 'project has valid output'() {
        given:
        def projectConnection = newConnector().forProjectDirectory(projectDir).connect()

        when:
        def output = runGradle(projectConnection, 'welcome').toString('UTF-8').split('\n')

        then:
        def helloIdx = output.findIndexOf { it == "Hello, ${System.properties['user.name']}" }
        def dateIdx = output.findIndexOf { it.matches('\\d{2}-\\d{2}-\\d{4}') }

        then:
        dateIdx == helloIdx + 1

        cleanup:
        projectConnection.close()
    }

    def 'task dependencies are validated'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.tasks.findByName('hello')
        project.tasks.findByName('user')
        project.tasks.findByName('welcome')
        project.tasks['welcome'].dependsOn.contains([project.tasks['hello'], project.tasks['user']])
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).withArguments('-q').run()
        output
    }
}
