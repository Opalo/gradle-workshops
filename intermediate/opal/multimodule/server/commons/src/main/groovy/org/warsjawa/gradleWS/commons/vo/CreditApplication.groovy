package org.warsjawa.gradleWS.commons.vo

import groovy.transform.ToString

@ToString(includeNames = true)
class CreditApplication implements Serializable {

    CreditApplication() {
    }

    Long id
    String firstName
    String lastName
    String email
    BigDecimal amount
    String purpose
}
