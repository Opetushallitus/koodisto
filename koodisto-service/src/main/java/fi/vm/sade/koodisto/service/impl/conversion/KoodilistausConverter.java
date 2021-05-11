package fi.vm.sade.koodisto.service.impl.conversion;

import fi.jhs_suositukset.skeemat.oph._2012._05._03.KieliType;
import fi.jhs_suositukset.skeemat.oph._2012._05._03.KoodiMetadataType;
import fi.jhs_suositukset.skeemat.oph._2012._05._03.KoodiType;
import fi.jhs_suositukset.skeemat.oph._2012._05._03.Koodilistaus;
import fi.jhs_suositukset.skeemat.oph._2012._05._03.KoodistoItemType;
import fi.jhs_suositukset.skeemat.oph._2012._05._03.TilaType;
import fi.vm.sade.koodisto.service.types.common.KoodiCollectionType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class KoodilistausConverter {

    /*
    Tämä luokka on hirvittävä kludge ja puhdasta pahuutta. Sori siitä!
    Koko hirvityksen olemassaolon oikeutus on, että KoodiCollectionType alityyppeineen esiintyy
    kahdessa eri namespacessa, mistä syntyy kaksi rinnakkaista luokkahierarkiaan eri packageihin.
    Kunhan web servicet saadaan luiskattua, jäljelle jää ainoastaan upload/download-toiminnon
    käyttämä skeema ja namespace, jolloin kaikki tämä roska voidaan poistaa.
     */

    private final ModelMapper modelMapper = new ModelMapper();

    public Koodilistaus koodilistausFrom(KoodiCollectionType koodiCollectionType) {
        Koodilistaus listaus = modelMapper.map(koodiCollectionType, Koodilistaus.class);
        listaus.getKoodi().addAll(
                koodiCollectionType.getKoodi().stream().map(this::koodilistausKoodiType).collect(Collectors.toList()));
        return listaus;
    }

    public KoodiCollectionType koodiCollectionFrom(Koodilistaus koodiListausType) {
        KoodiCollectionType collection =  modelMapper.map(koodiListausType, KoodiCollectionType.class);
        collection.getKoodi().addAll(
                koodiListausType.getKoodi().stream().map(this::commonKoodiType).collect(Collectors.toList()));
        return collection;
    }

    fi.vm.sade.koodisto.service.types.common.KoodiType commonKoodiType(KoodiType koodiType) {
        if (koodiType == null) return null;
        fi.vm.sade.koodisto.service.types.common.KoodiType converted = modelMapper.map(
                koodiType, fi.vm.sade.koodisto.service.types.common.KoodiType.class);
        converted.getMetadata().addAll(
                koodiType.getMetadata().stream().map(this::commonKoodiMetadataType).collect(Collectors.toList()));
        converted.setKoodisto(commonKoodistoItemType(koodiType.getKoodisto()));
        converted.setTila(fi.vm.sade.koodisto.service.types.common.TilaType.valueOf(koodiType.getTila().value()));
        return converted;
    }

    fi.vm.sade.koodisto.service.types.common.KoodiMetadataType commonKoodiMetadataType(KoodiMetadataType metadata) {
        if (metadata == null) return null;
        fi.vm.sade.koodisto.service.types.common.KoodiMetadataType converted = modelMapper.map(
                metadata, fi.vm.sade.koodisto.service.types.common.KoodiMetadataType.class);
        converted.setKieli(fi.vm.sade.koodisto.service.types.common.KieliType.valueOf(metadata.getKieli().value()));
        return converted;
    }

    fi.vm.sade.koodisto.service.types.common.KoodistoItemType commonKoodistoItemType(KoodistoItemType itemType) {
        if (itemType == null) return null;
        fi.vm.sade.koodisto.service.types.common.KoodistoItemType converted = modelMapper.map(
                itemType, fi.vm.sade.koodisto.service.types.common.KoodistoItemType.class);
        converted.getKoodistoVersio().addAll(itemType.getKoodistoVersio());
        return converted;
    }

    KoodiType koodilistausKoodiType(fi.vm.sade.koodisto.service.types.common.KoodiType koodiType) {
        if (koodiType == null) return null;
        KoodiType converted = modelMapper.map(koodiType, KoodiType.class);
        converted.getMetadata().addAll(
                koodiType.getMetadata().stream().map(this::koodilistausMetadataType).collect(Collectors.toList()));
        converted.setKoodisto(koodilistausKoodistoItemType(koodiType.getKoodisto()));
        converted.setTila(TilaType.valueOf(koodiType.getTila().value()));
        return converted;
    }

    KoodiMetadataType koodilistausMetadataType(fi.vm.sade.koodisto.service.types.common.KoodiMetadataType metadata) {
        if (metadata == null) return null;
        KoodiMetadataType converted = modelMapper.map(metadata, KoodiMetadataType.class);
        converted.setKieli(KieliType.valueOf(metadata.getKieli().value()));
        return converted;
    }

    KoodistoItemType koodilistausKoodistoItemType(fi.vm.sade.koodisto.service.types.common.KoodistoItemType itemType) {
        if (itemType == null) return null;
        KoodistoItemType converted = modelMapper.map(itemType, KoodistoItemType.class);
        converted.getKoodistoVersio().addAll(itemType.getKoodistoVersio());
        return converted;
    }


}
