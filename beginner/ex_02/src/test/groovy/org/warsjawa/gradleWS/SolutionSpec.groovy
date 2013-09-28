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
        def output = runGradle(projectConnection, 'helloDefault', 'helloCustom').toString('UTF-8').split('\n')

        then:
        println output
        output.find { it == "Hello from Gradle" }
        output.find { it == "Hello from me" }

        cleanup:
        projectConnection.close()
    }

    def 'tasks have valid declarations'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        def helloDefault = project.tasks.findByName('helloDefault')
        def helloCustom = project.tasks.findByName('helloCustom')

        def tasks = [helloDefault, helloCustom]

        and:
        tasks.every { it != null }

        and:
        tasks.every { it.class.superclass.simpleName == 'Greeting' }

        and:
        helloDefault.greeting == 'Hello from Gradle'
        helloCustom.greeting == 'Hello from me'
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).run()
        output
    }
}
