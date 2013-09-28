package org.warsjawa.gradleWS

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.testfixtures.ProjectBuilder.builder

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')

    def 'content is zipped and then unzipped'() {
        given:
        def connection = GradleConnector.newConnector().forProjectDirectory(projectDir).connect()
        def project = builder().withProjectDir(projectDir).build()

        and:
        project.evaluate()
        project.buildDir.deleteDir()

        when:
        runGradle(connection, 'zip', 'unzip')

        then:
        def zip = new File(project.buildDir, 'distributions/content.zip')
        zip.exists()
        zip.isFile()

        then:
        def unpack = new File(project.buildDir, 'unpack')
        unpack.exists()
        unpack.isDirectory()
        unpack.list().sort() == ['ch1', 'ch2', 'content.txt', 'content.xml']
        new File(unpack, 'ch1').list().sort() == ['ch1.txt', 'ch1.xml', 's1']
        new File(unpack, 'ch1/s1').list().sort() == ['s1.txt', 's1.xml']
        new File(unpack, 'ch2').list().sort() == ['ch2.txt', 'ch2.xml']

        cleanup:
        connection.close()
    }

    def 'tasks are declared with appropriate types'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.tasks.findByName('zip').class.superclass.simpleName == 'Zip'
        project.tasks.findByName('unzip').class.superclass.simpleName == 'Copy'
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        buildLauncher.forTasks(tasks).run()
    }
}
