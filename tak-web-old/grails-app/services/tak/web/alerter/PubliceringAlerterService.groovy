package tak.web.alerter

import se.skltp.tak.core.entity.PubVersion

interface PubliceringAlerterService {
    void alertOnPublicering(PubVersion pubVersionInstance);
    void alertOnRollback(PubVersion pubVersionInstance)

}
