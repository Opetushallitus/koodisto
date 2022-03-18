package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;

import java.util.Optional;
public class KoodiTypeToKoodiVersioConverter extends AbstractToDomainConverter<KoodiType, KoodiVersio> {

    @Override
    public KoodiVersio convert(KoodiType source) {
        KoodiVersio versio = new KoodiVersio();
        versio.setKoodiarvo(source.getKoodiArvo());

        if (source.getPaivitysPvm() != null) {
            versio.setPaivitysPvm(source.getPaivitysPvm());
        }
        Optional.ofNullable(source.getPaivittajaOid()).ifPresent(versio::setPaivittajaOid);
        if (source.getTila() != null) {
            versio.setTila(Tila.valueOf(source.getTila().name()));
        }
        versio.setVersio(source.getVersio());
        if (source.getVoimassaAlkuPvm() != null) {
            versio.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm());
        }

        if (source.getVoimassaLoppuPvm() != null) {
            versio.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm());
        }

        versio.setVersio(source.getVersio());
        return versio;
    }

}
