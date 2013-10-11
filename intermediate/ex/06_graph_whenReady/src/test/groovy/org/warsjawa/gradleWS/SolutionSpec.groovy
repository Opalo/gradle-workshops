package org.warsjawa.gradleWS

import org.gradle.tooling.ProjectConnection
import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.testfixtures.ProjectBuilder.builder
import static org.gradle.tooling.GradleConnector.newConnector

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')

    def 'no exception thrown when other task is run'() {
        given:
        def connection = newConnector().forProjectDirectory(projectDir).connect()

        when:
        runGradle(connection, 'tasks')

        then:
        noExceptionThrown()

        cleanup:
        connection.close()
    }

    def 'no exception thrown when upload task is run and url property is set'() {
        given:
        def connection = newConnector().forProjectDirectory(projectDir).connect()

        when:
        runGradle(connection, 'upload', 'url')

        then:
        noExceptionThrown()

        cleanup:
        connection.close()
    }

    def 'exception thrown when upload task is run and no url property is set'() {
        given:
        def connection = newConnector().forProjectDirectory(projectDir).connect()

        when:
        runGradle(connection, 'upload')

        then:
        def e = thrown(Exception)
        e.cause.cause.cause.message == 'uploadUrl must be set to execute upload!'
        cleanup:
        connection.close()
    }


    def 'upload task is declared'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.tasks.findByName('upload')
    }

    def runGradle(ProjectConnection pc, String task, String url = null) {
        def buildLauncher = pc.newBuild()
        String[] args = url ? ["-PuploadUrl=url"] : []
        buildLauncher.forTasks(task).withArguments(args).run()
    }
}
