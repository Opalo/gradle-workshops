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

    def 'wsConf configuration is added'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.configurations.getByName('wsConf')
    }

    def 'httpclient dependency is added'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.configurations.getByName('wsConf').dependencies.collect {
            "$it.group:$it.name:$it.version".toString()
        }.contains('org.apache.httpcomponents:httpclient:4.0.3')
    }

    def 'all tasks added with valid types'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.tasks['showDeps']
        project.tasks['copyDeps'].class.superclass.simpleName == 'Copy'
    }

    def 'showDeps task generates valid output'() {
        when:
        def output = runGradle(projectConnection, 'showDeps').toString('UTF-8').split('\n')

        then:
        output.collect { new File(it) }*.name.contains('httpclient-4.0.3.jar')
    }

    def 'copyDeps copies all dependencies to buildDir'() {
        given:
        def project = builder().withProjectDir(projectDir).build()
        project.buildDir.deleteDir()

        and:
        project.evaluate()

        when:
        runGradle(projectConnection, 'copyDeps')

        then:
        def dep = new File(project.buildDir, 'deps/httpclient-4.0.3.jar')
        dep.exists()
        dep.isFile()
        dep.size() > 0
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).withArguments('-q').run()
        output
    }
}
