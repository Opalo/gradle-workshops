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

    def 'countSize configured with appropriate type and properties'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        def task = project.tasks['countSize']
        task.class.superclass.simpleName == 'Size'

        and:
        task.inputs.hasInputs
        task.inputs.files.files*.name.containsAll(['sample1.txt', 'sample2.txt'])

        and:
        task.outputs.hasOutput
        def output = task.outputs.files.singleFile
        output.parentFile == project.buildDir
        output.name == 'size.txt'
    }

    def 'countSize task is run and appropriate output is generated'() {
        given:
        def project = builder().withProjectDir(projectDir).build()
        def connection = newConnector().forProjectDirectory(projectDir).connect()

        when:
        runGradle(connection, 'countSize')

        then:
        def size = project.file("$project.buildDir/size.txt")
        size.text == '32'

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
