package fi.jhs_suositukset.skeemat.oph._2012._05._03;

import fi.vm.sade.koodisto.service.types.common.KoodiType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "koodilistaus", namespace = "http://skeemat.jhs-suositukset.fi/oph/2012/05/03")
@XmlType
public class KoodiListaus {
    @XmlElement(name = "koodi")
    private List<KoodiType> koodit = new ArrayList<>();

    public List<KoodiType> getKoodi() {
        return koodit;
    }
}
