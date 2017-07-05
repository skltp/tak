package tak.web.alerter

import se.skltp.tak.core.entity.PubVersion

interface PubliceringAlerterService {
    void alert(PubVersion pubVersionInstance);

}
