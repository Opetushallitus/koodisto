package fi.vm.sade.koodisto.service.impl.conversion.koodi;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import org.springframework.stereotype.Component;

@Component("koodiTypeToKoodiVersioConverter")
public class KoodiTypeToKoodiVersioConverter extends AbstractToDomainConverter<KoodiType, KoodiVersio> {

    @Override
    public KoodiVersio convert(KoodiType source) {
        KoodiVersio versio = new KoodiVersio();
        versio.setKoodiarvo(source.getKoodiArvo());

        if (source.getPaivitysPvm() != null) {
            versio.setPaivitysPvm(DateHelper.xmlCalToDate(source.getPaivitysPvm()));
        }
        if (source.getTila() != null) {
            versio.setTila(Tila.valueOf(source.getTila().name()));
        }
        versio.setVersio(source.getVersio());
        if (source.getVoimassaAlkuPvm() != null) {
            versio.setVoimassaAlkuPvm(DateHelper.xmlCalToDate(source.getVoimassaAlkuPvm()));
        }

        if (source.getVoimassaLoppuPvm() != null) {
            versio.setVoimassaLoppuPvm(DateHelper.xmlCalToDate(source.getVoimassaLoppuPvm()));
        }

        versio.setVersio(source.getVersio());
        return versio;
    }

}
