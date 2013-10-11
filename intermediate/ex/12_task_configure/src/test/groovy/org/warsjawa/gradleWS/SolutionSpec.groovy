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

    def 'all tasks configured with appropriate type'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        when:
        project.evaluate()

        then:
        project.tasks['showOpenSourceLibs']
        project.tasks['freeImages'].class.superclass.simpleName == 'Zip'
        project.tasks['freeImages'].license == 'GPL'
        project.tasks['royaltyImages'].class.superclass.simpleName == 'Zip'
        project.tasks['royaltyImages'].license == 'commercial'
        project.tasks['publicDocs'].class.superclass.simpleName == 'Zip'
        project.tasks['publicDocs'].license == 'LGPL'
        project.tasks['enterpriseDocs'].class.superclass.simpleName == 'Zip'
        project.tasks['enterpriseDocs'].license == 'commercial'
        project.tasks['eclipseLibs'].class.superclass.simpleName == 'Zip'
        project.tasks['eclipseLibs'].license == 'EPL'
        project.tasks['oracleLibs'].class.superclass.simpleName == 'Zip'
        project.tasks['oracleLibs'].license == 'commercial'
    }

    def 'showOpenSourceLibs task generates valid output'() {
        when:
        def output = runGradle(projectConnection, 'showOpenSourceLibs').toString('UTF-8').split('\n')

        then:
        output.sort() == ["task ':eclipseLibs'", "task ':freeImages'", "task ':publicDocs'"]
    }

    def runGradle(ProjectConnection pc, String... tasks) {
        def buildLauncher = pc.newBuild()
        def output = new ByteArrayOutputStream()
        buildLauncher.standardOutput = output
        buildLauncher.forTasks(tasks).withArguments('-q').run()
        output
    }
}
