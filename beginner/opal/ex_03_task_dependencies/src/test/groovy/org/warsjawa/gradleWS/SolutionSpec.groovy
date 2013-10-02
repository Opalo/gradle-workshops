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
        def output = runGradle(projectConnection, 'date').toString('UTF-8').split('\n')

        then:
        def idx1 = output.findIndexOf { it == 'top level 1' }
        def idx2 = output.findIndexOf { it == 'date configuration' }
        def idx3 = output.findIndexOf { it == 'top level 2' }
        def idx4 = output.findIndexOf { it == 'Hello World' }
        def idx5 = output.findIndexOf { it.matches("\\d{2}-\\d{2}-\\d{4}") }
        def indexes = [idx1, idx2, idx3, idx4, idx5]
        indexes.every { it > -1 }
        indexes.sort(false) == indexes

        cleanup:
        projectConnection.close()
    }

    def 'project has valid tasks with dependencies'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        def hello = project.tasks.findByName('hello')
        def date = project.tasks.findByName('date')
        hello
        date
        date.dependsOn.contains(hello)

    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).run()
        output
    }
}
