package org.warsjawa.gradleWS.commons.vo

class CreditApplication implements Serializable {

    CreditApplication() {
    }

    Long id
    String firstName
    String lastName
    String email
    BigDecimal amount
    String purpose

    @Override
    public String toString() {
        id.toString()
    }
}
