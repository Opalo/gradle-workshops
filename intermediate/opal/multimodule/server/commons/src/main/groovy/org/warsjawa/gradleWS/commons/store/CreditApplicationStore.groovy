package org.warsjawa.gradleWS.commons.store

import org.warsjawa.gradleWS.commons.vo.CreditApplication

interface CreditApplicationStore {

    Long saveCreditApplication(CreditApplication ca)

    CreditApplication creditApplicationForId(Long id)

    List<CreditApplication> list()

    void delete(Long id)
}