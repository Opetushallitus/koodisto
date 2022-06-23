package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaMetadataDto;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;

import java.util.Collection;

public interface UriTransliterator {

    String generateKoodiUriByKoodistoUriAndKoodiArvo(String koodistoUri, String koodiArvo);

    String generateKoodistoUriByMetadata(Collection<KoodistoMetadataType> metadatas);

    String generateKoodistoGroupUriByMetadata(Collection<KoodistoRyhmaMetadataDto> metadatas);
}
