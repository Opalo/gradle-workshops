package org.warsjawa.gradleWS

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.testfixtures.ProjectBuilder.builder

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')
    @Shared projectConnection = GradleConnector.newConnector().forProjectDirectory(projectDir).connect()

    def cleanupSpec() {
        projectConnection.close()
    }

    def 'all tasks declared with valid type'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.tasks['showFiles']
        project.tasks['copyFiles'].class.superclass.simpleName == 'Copy'
        project.tasks['copyTree'].class.superclass.simpleName == 'Copy'
    }

    def 'showFiles task generates valid output'() {
        when:
        def output = runGradle(projectConnection, 'showFiles').toString('UTF-8').split('\n')

        then:
        def files = [
                'content/ch1/ch1.txt',
                'content/ch1/s1/s1.txt',
                'content/ch2/ch2.txt',
                'content/content.txt'
        ].collect {
            new File(it).absolutePath
        }.sort()
        output.sort() == files
    }

    def 'copyFiles copies all files to buildDir'() {
        given:
        def project = builder().withProjectDir(projectDir).build()
        project.buildDir.deleteDir()

        and:
        project.evaluate()

        when:
        runGradle(projectConnection, 'copyFiles')

        then:
        def files = new File(project.buildDir, 'files')
        files.list().sort() == ['ch1.txt', 'ch2.txt', 'content.txt', 's1.txt']
    }

    def 'copyTree copies all files to buildDir preserving dir structure'() {
        given:
        def project = builder().withProjectDir(projectDir).build()
        project.buildDir.deleteDir()

        and:
        project.evaluate()

        when:
        runGradle(projectConnection, 'copyTree')

        then:
        def fileTree = new File(project.buildDir, 'fileTree')
        fileTree.list().sort() == ['ch1', 'ch2', 'content.txt']
        new File(fileTree, 'ch1').list().sort() == ['ch1.txt', 's1']
        new File(fileTree, 'ch1/s1').list().sort() == ['s1.txt']
        new File(fileTree, 'ch2').list().sort() == ['ch2.txt']
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).withArguments('-q').run()
        output
    }
}
