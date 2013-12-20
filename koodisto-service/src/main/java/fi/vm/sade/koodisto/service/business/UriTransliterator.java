package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;

import java.util.Collection;

/**
 * User: kwuoti
 * Date: 21.3.2013
 * Time: 15.59
 */
public interface UriTransliterator {

    String generateKoodiUriByKoodistoUriAndKoodiArvo(String koodistoUri, String koodiArvo);

    String generateKoodistoUriByMetadata(Collection<KoodistoMetadataType> metadatas);
}
