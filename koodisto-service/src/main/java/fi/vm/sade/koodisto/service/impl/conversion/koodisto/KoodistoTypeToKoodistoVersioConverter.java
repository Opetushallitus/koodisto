package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import org.springframework.stereotype.Component;

@Component("koodistoTypeToKoodistoVersioConverter")
public class KoodistoTypeToKoodistoVersioConverter extends AbstractToDomainConverter<KoodistoType, KoodistoVersio> {

    @Override
    public KoodistoVersio convert(KoodistoType source) {

        KoodistoVersio kv = new KoodistoVersio();

        if (source.getPaivitysPvm() != null) {
            kv.setPaivitysPvm(DateHelper.xmlCalToDate(source.getPaivitysPvm()));
        }

        kv.setTila(Tila.valueOf(source.getTila().name()));
        kv.setVersio(source.getVersio());

        if (source.getVoimassaAlkuPvm() != null) {
            kv.setVoimassaAlkuPvm(DateHelper.xmlCalToDate(source.getVoimassaAlkuPvm()));
        }

        if (source.getVoimassaLoppuPvm() != null) {
            kv.setVoimassaLoppuPvm(DateHelper.xmlCalToDate(source.getVoimassaLoppuPvm()));
        }
        return kv;
    }

}
