package org.warsjawa.gradleWS

import org.gradle.tooling.ProjectConnection
import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.testfixtures.ProjectBuilder.builder
import static org.gradle.tooling.GradleConnector.newConnector

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')

    def 'content is copied'() {
        given:
        def connection = newConnector().forProjectDirectory(projectDir).connect()
        def result = new File('result')

        expect:
        !result.exists()

        when:
        runGradle(connection, 'copyContent')

        then:
        result.isDirectory()
        result.exists()
        ['sample1', 'sample2', 'sample1.txt', 'sample2.txt', 'sample1.data', 'sample2.data',].every {
            new File(result, it).exists()
        }

        cleanup:
        result.deleteDir()
        connection.close()
    }

    def 'copy task is of type Copy'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.tasks.findByName('copyContent').class.superclass.simpleName == 'Copy'
    }

    def runGradle(ProjectConnection pc, String task) {
        def buildLauncher = pc.newBuild()
        buildLauncher.forTasks(task).run()
    }
}
