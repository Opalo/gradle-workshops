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

    def 'group name and version is set and maven plugin is applied'() {
        given:
        def project = builder().withProjectDir(projectDir).withName('simple').build()


        when:
        project.evaluate()

        then:
        project.group == 'org.warsjawa'
        project.version == '3.0'
        project.name == 'simple'

        and:
        project.plugins.findPlugin('maven')
    }

    def 'uploadArchives installs artifact in maven repo'() {
        given:
        def repos = new File(projectDir, 'repos')
        repos.deleteDir()

        expect:
        !repos.exists()

        when:
        runGradle(projectConnection, 'uploadArchives')

        then:
        repos.exists()
        repos.isDirectory()
        def root = new File(repos, 'org/warsjawa/simple')

        ['simple-3.0.jar', 'simple-3.0.jar.md5', 'simple-3.0.jar.sha1',
                'simple-3.0.pom', 'simple-3.0.pom.md5', 'simple-3.0.pom.sha1'].every {
            def f = new File(root, "3.0/$it")
            f.exists() && f.isFile() && f.size() > 0
        }

        then:
        ['maven-metadata.xml', 'maven-metadata.xml.md5', 'maven-metadata.xml.sha1'].every {
            def f = new File(root, it)
            f.exists() && f.isFile() && f.size() > 0
        }

        when:
        new XmlSlurper().parseText(new File(root, 'maven-metadata.xml').text)

        then:
        noExceptionThrown()
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).withArguments('-q').run()
        output
    }
}
