package fi.vm.sade.koodisto.service.impl;

import fi.vm.sade.generic.rest.Cacheable;
import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.common.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import java.util.List;

@Component
@Path("/")
@Produces(MediaType.APPLICATION_XML)
public class KoodistoRESTService {

    private static final String KOODI_VERSIO = "koodiVersio";
    private static final String KOODI_URI = "koodiUri";
    private static final String KOODI_ARVO = "koodiArvo";
    private static final String KOODISTO_VERSIO = "koodistoVersio";
    private static final String KOODISTO_URI = "koodistoUri";

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @GET
    public JAXBElement<KoodistoRyhmaCollectionType> listAllKoodistoRyhmas() {
        KoodistoRyhmaCollectionType collection = new KoodistoRyhmaCollectionType();
        collection.getKoodistoryhma().addAll(conversionService.convertAll(
                koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListType.class));

        return new ObjectFactory().createKoodistoRyhmaCollection(collection);
    }

    @GET
    @Path("/{koodistoUri}")
    public JAXBElement<KoodistoType> getKoodistoByUri(@PathParam(KOODISTO_URI) String koodistoUri,
                                                      @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {

        KoodistoVersio koodisto = null;
        if (koodistoVersio == null) {
            koodisto = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        } else {
            koodisto = koodistoBusinessService.getKoodistoVersio(koodistoUri, koodistoVersio);
        }

        return new ObjectFactory().createKoodisto(conversionService.convert(koodisto, KoodistoType.class));
    }

    @GET
    @Path("/{koodistoUri}/koodi")
    public JAXBElement<KoodiCollectionType> getKoodisByKoodisto(@PathParam(KOODISTO_URI) String koodistoUri,
                                                                @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {

        List<KoodiVersioWithKoodistoItem> koodis = null;
        if (koodistoVersio == null) {
            koodis = koodiBusinessService.getKoodisByKoodisto(koodistoUri, false);
        } else {
            koodis = koodiBusinessService.getKoodisByKoodistoVersio(koodistoUri, koodistoVersio, false);
        }

        KoodiCollectionType collection = new KoodiCollectionType();
        collection.getKoodi().addAll(conversionService.convertAll(koodis, KoodiType.class));

        return new ObjectFactory().createKoodiCollection(collection);
    }

    @GET
    @Path("/{koodistoUri}/koodi/arvo/{koodiArvo}")
    public JAXBElement<KoodiCollectionType> getKoodisByArvo(@PathParam(KOODISTO_URI) String koodistoUri,
                                                            @PathParam(KOODI_ARVO) String koodiArvo,
                                                            @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {
        List<KoodiVersioWithKoodistoItem> koodis = null;
        if (koodistoVersio == null) {
            koodis = koodiBusinessService.getKoodisByKoodistoWithKoodiArvo(koodistoUri, koodiArvo);
        } else {
            koodis = koodiBusinessService.getKoodisByKoodistoVersioWithKoodiArvo(koodistoUri, koodistoVersio, koodiArvo);
        }

        KoodiCollectionType collection = new KoodiCollectionType();
        collection.getKoodi().addAll(conversionService.convertAll(koodis, KoodiType.class));

        return new ObjectFactory().createKoodiCollection(collection);
    }

    @GET
    @Path("/{koodistoUri}/koodi/{koodiUri}")
    public JAXBElement<KoodiType> getKoodiByUri(@PathParam(KOODISTO_URI) String koodistoUri,
                                                @PathParam(KOODI_URI) String koodiUri,
                                                @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {
        KoodiVersioWithKoodistoItem koodi;
        if (koodistoVersio == null) {
            koodi = koodiBusinessService.getKoodiByKoodisto(koodistoUri, koodiUri);
        } else {
            koodi = koodiBusinessService.getKoodiByKoodistoVersio(koodistoUri, koodistoVersio, koodiUri);
        }

        return new ObjectFactory().createKoodi(conversionService.convert(koodi, KoodiType.class));
    }

    @GET
    @Path("/relaatio/sisaltyy-alakoodit/{koodiUri}")
    public JAXBElement<KoodiCollectionType> getAlakoodis(@PathParam(KOODI_URI) String koodiUri,
                                                         @QueryParam(KOODI_VERSIO) Integer koodiVersio) {

        final boolean isChild = false;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.SISALTYY;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    @GET
    @Path("/relaatio/sisaltyy-ylakoodit/{koodiUri}")
    public JAXBElement<KoodiCollectionType> getYlakoodis(@PathParam(KOODI_URI) String koodiUri,
                                                         @QueryParam(KOODI_VERSIO) Integer koodiVersio) {

        final boolean isChild = true;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.SISALTYY;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    @GET
    @Path("/relaatio/rinnasteinen/{koodiUri}")
    public JAXBElement<KoodiCollectionType> getRinnasteinenKoodis(@PathParam(KOODI_URI) String koodiUri,
                                                                  @QueryParam(KOODI_VERSIO) Integer koodiVersio) {

        final boolean isChild = false;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.RINNASTEINEN;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    protected JAXBElement<KoodiCollectionType> getRelations(String koodiUri, Integer koodiVersio,
                                                            SuhteenTyyppi suhteenTyyppi, boolean isChild) {
        List<KoodiVersioWithKoodistoItem> koodis = null;
        if (koodiVersio == null) {
            koodis = koodiBusinessService.listByRelation(koodiUri, isChild, suhteenTyyppi);
        } else {
            koodis = koodiBusinessService.listByRelation(koodiUri, koodiVersio, isChild, suhteenTyyppi);
        }

        KoodiCollectionType collection = new KoodiCollectionType();
        collection.getKoodi().addAll(conversionService.convertAll(koodis, KoodiType.class));

        return new ObjectFactory().createKoodiCollection(collection);
    }

    @GET
    @Path("/{koodistoUri}.xsd")
    @Cacheable(maxAgeSeconds = KoodistoJsonRESTService.ONE_HOUR)
    @Transactional
    public String getKoodistoXsdSkeema(@PathParam(KOODISTO_URI) String koodistoUri, @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {
        KoodistoVersio koodisto = null;
        if (koodistoVersio == null) {
            koodisto = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        } else {
            koodisto = koodistoBusinessService.getKoodistoVersio(koodistoUri, koodistoVersio);
        }

        // todo: streamaus vois olla kohdillaan kun esim posti -koodistosta tulee 1,4 meganen xsd-dokkari
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "           targetNamespace=\"http://service.koodisto.sade.vm.fi/types/koodisto\"\n" +
                "           xmlns=\"http://service.koodisto.sade.vm.fi/types/koodisto\"\n" +
                "           elementFormDefault=\"qualified\">\n" +
                "\n" +
                "    <xs:simpleType name=\"%s\">\n" +
                "        <xs:restriction base=\"xs:string\">\n", escapeXml(koodistoUri, true)));
        for (KoodistoVersioKoodiVersio koodiVersio : koodisto.getKoodiVersios()) {
            sb.append(String.format(
                "            <xs:enumeration value=\"%s\">\n" +
                "                <xs:annotation>\n",
                    escapeXml(koodiVersio.getKoodiVersio().getKoodiarvo(), false)));
            for (KoodiMetadata metadata : koodiVersio.getKoodiVersio().getMetadatas()) {
                sb.append(String.format(
                        "                    <xs:documentation xml:lang=\"%s\">%s</xs:documentation>\n",
                        metadata.getKieli().toString().toLowerCase(), escapeXml(metadata.getNimi(), false)));
            } // for koodi metadatas
            sb.append(
                "                </xs:annotation>\n" +
                "            </xs:enumeration>\n" +
                "\n");
        } // for koodis
        sb.append(
                "        </xs:restriction>\n" +
                "    </xs:simpleType>\n" +
                "\n" +
                "</xs:schema>\n");
        return sb.toString();
    }

    private String escapeXml(Object o, boolean isTypeName) {
        if (o == null){
            return "";
        }
        String s = StringEscapeUtils.escapeXml(o.toString());
        if (isTypeName) {
            s = s.replaceAll(":","").replaceAll("/",""); // cannot have these chars in <xs:simpleType name="..."
        }
        return s;
    }

}
