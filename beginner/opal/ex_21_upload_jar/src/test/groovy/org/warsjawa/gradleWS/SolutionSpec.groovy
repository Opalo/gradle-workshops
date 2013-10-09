package org.warsjawa.gradleWS

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import spock.lang.Shared
import spock.lang.Specification

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')
    @Shared projectConnection = GradleConnector.newConnector().forProjectDirectory(projectDir).connect()

    def cleanupSpec() {
        projectConnection.close()
    }

    def 'jar file is found under repos dir when uploadArchives invoked'() {
        given:
        def repos = new File(projectDir, 'repos')
        def project = ProjectBuilder.builder().withProjectDir(projectDir).build()

        expect:
        repos.deleteDir()
        !repos.exists()

        when:
        runGradle(projectConnection, 'uploadArchives')

        then:
        def jar = new File(repos, 'ex_21_upload_jar-unspecified.jar')
        jar.exists()
        jar.isFile()
        jar.size() > 2900
        jar.size() < 3000

        and:
        !project.tasks.findByName('uploadArchives')
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).withArguments('-q').run()
        output
    }
}
