package org.warsjawa.gradleWS

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testfixtures.ProjectBuilder.builder

class SolutionSpec extends Specification {

    @Shared projectDir = new File('.')

    def 'updateVersion task is enabled when numeric app_version property is set'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        and:
        project.ext['app_version'] = version

        when:
        project.evaluate()

        then:
        project.tasks.findByName('updateVersion')
        project.tasks.findByName('updateVersion').enabled == enabled

        where:
        version | enabled
        ''      | false
        null    | false
        'a'     | false
        '10'    | true
    }

    @Unroll
    def 'updateAuthor runs when non-empty app_author system property is set'() {
        given:
        def project = builder().withProjectDir(projectDir).build()

        and:
        System.properties.put('app_author', author)

        when:
        project.evaluate()

        then:
        def task = project.tasks.findByName('updateAuthor')
        task
        task.onlyIf.isSatisfiedBy(task) == onlyIf

        cleanup:
        System.properties.remove('app_author')

        where:
        author | onlyIf
        ''     | false
        'a'    | true
    }
}
