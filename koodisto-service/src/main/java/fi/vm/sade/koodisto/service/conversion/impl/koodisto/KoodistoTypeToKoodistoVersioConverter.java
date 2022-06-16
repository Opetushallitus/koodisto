package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("koodistoTypeToKoodistoVersioConverter")
public class KoodistoTypeToKoodistoVersioConverter implements AbstractToDomainConverter<KoodistoType, KoodistoVersio> {

    @Override
    public KoodistoVersio convert(KoodistoType source) {

        KoodistoVersio kv = new KoodistoVersio();

        if (source.getPaivitysPvm() != null) {
            kv.setPaivitysPvm(source.getPaivitysPvm());
        }
        Optional.ofNullable(source.getPaivittajaOid()).ifPresent(kv::setPaivittajaOid);

        kv.setTila(Tila.valueOf(source.getTila().name()));
        kv.setVersio(source.getVersio());

        if (source.getVoimassaAlkuPvm() != null) {
            kv.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm());
        }

        if (source.getVoimassaLoppuPvm() != null) {
            kv.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm());
        }
        return kv;
    }

}
