package org.warsjawa.gradleWS

import spock.lang.Specification

class ExSpec extends Specification {

    def 'success test'() {
        expect:
        Thread.sleep(30)
        1
    }
}
