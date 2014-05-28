package fi.vm.sade.koodisto.service.impl;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.generic.rest.Cacheable;
import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.common.KoodiCollectionType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaCollectionType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.ObjectFactory;

@Component
@Path("/")
@Produces(MediaType.APPLICATION_XML)
@Api(value = "/rest", description = "REST/XML rajapinta")
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
    @ApiOperation(
            value = "Listaa kaikki koodistoryhmät",
            notes = "", response = KoodistoRyhmaCollectionType.class,
            responseContainer = "List")
    public JAXBElement<KoodistoRyhmaCollectionType> listAllKoodistoRyhmas() {
        KoodistoRyhmaCollectionType collection = new KoodistoRyhmaCollectionType();
        collection.getKoodistoryhma().addAll(conversionService.convertAll(koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListType.class));

        return new ObjectFactory().createKoodistoRyhmaCollection(collection);
    }

    @GET
    @Path("/{koodistoUri}")
    @ApiOperation(
            value = "Lataa koodisto URIn perusteella",
            notes = "",
            response = KoodistoType.class)
    public JAXBElement<KoodistoType> getKoodistoByUri(
            @ApiParam(value = "Koodiston URI") @PathParam(KOODISTO_URI) String koodistoUri,
            @ApiParam(value = "Koodiston versio") @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {

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
    @ApiOperation(
            value = "Listaa koodiston kaikki koodit", 
            notes = "", 
            response = KoodiCollectionType.class)
    public JAXBElement<KoodiCollectionType> getKoodisByKoodisto(
            @ApiParam(value = "Koodiston URI") @PathParam(KOODISTO_URI) String koodistoUri,
            @ApiParam(value = "Koodiston versio") @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {

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
    @ApiOperation(
            value = "Listaa koodit arvon perusteella", 
            notes = "Nykyisellä toteutuksella palauttaa vain yhden koodin.", 
            response = KoodiCollectionType.class)
    public JAXBElement<KoodiCollectionType> getKoodisByArvo(
            @ApiParam(value = "Koodiston URI") @PathParam(KOODISTO_URI) String koodistoUri,
            @ApiParam(value = "Koodin arvo") @PathParam(KOODI_ARVO) String koodiArvo,
            @ApiParam(value = "Koodiston versio") @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {
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
    @ApiOperation(
            value = "Lataa koodi URIn perusteella",
            notes = "",
            response = KoodiType.class)
    public JAXBElement<KoodiType> getKoodiByUri(
            @ApiParam(value = "Koodiston URI") @PathParam(KOODISTO_URI) String koodistoUri,
            @ApiParam(value = "Koodin URI") @PathParam(KOODI_URI) String koodiUri,
            @ApiParam(value = "Koodiston versio") @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {
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
    @ApiOperation(
            value = "Listaa koodin sisältämät koodit", 
            notes = "Lista koodeista, joilla on SISÄLTYY-relaatio annettuun koodiin.", 
            response = KoodiCollectionType.class)
    public JAXBElement<KoodiCollectionType> getAlakoodis(
            @ApiParam(value = "Koodin URI") @PathParam(KOODI_URI) String koodiUri,
            @ApiParam(value = "Koodin versio") @QueryParam(KOODI_VERSIO) Integer koodiVersio) {

        final boolean isChild = false;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.SISALTYY;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    @GET
    @Path("/relaatio/sisaltyy-ylakoodit/{koodiUri}")
    @ApiOperation(
            value = "Listaa koodit, joihin koodi sisältyy",
            notes = "Lista koodeista, joilla on SISÄLTÄÄ-relaatio annettuun koodiin.",
            response = KoodiCollectionType.class)
    public JAXBElement<KoodiCollectionType> getYlakoodis(
            @ApiParam(value = "Koodin URI") @PathParam(KOODI_URI) String koodiUri,
            @ApiParam(value = "Koodin versio") @QueryParam(KOODI_VERSIO) Integer koodiVersio) {

        final boolean isChild = true;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.SISALTYY;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    @GET
    @Path("/relaatio/rinnasteinen/{koodiUri}")
    @ApiOperation(
            value = "Listaa koodiin rinnastuvat koodit",
            notes = "Lista koodeista, joilla on RINNASTUU-relaatio annettuun koodiin.",
            response = KoodiCollectionType.class)
    public JAXBElement<KoodiCollectionType> getRinnasteinenKoodis(
            @ApiParam(value = "Koodin URI") @PathParam(KOODI_URI) String koodiUri,
            @ApiParam(value = "Koodin versio") @QueryParam(KOODI_VERSIO) Integer koodiVersio) {

        final boolean isChild = false;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.RINNASTEINEN;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    protected JAXBElement<KoodiCollectionType> getRelations(String koodiUri, Integer koodiVersio, SuhteenTyyppi suhteenTyyppi, boolean isChild) {
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
    @ApiOperation(
            value = "Lataa koodiston XML malli",
            notes = "",
            response = String.class)
    public String getKoodistoXsdSkeema(
            @ApiParam(value = "Koodiston URI") @PathParam(KOODISTO_URI) String koodistoUri,
            @ApiParam(value = "Koodiston versio") @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {
        KoodistoVersio koodisto = null;
        if (koodistoVersio == null) {
            koodisto = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        } else {
            koodisto = koodistoBusinessService.getKoodistoVersio(koodistoUri, koodistoVersio);
        }

        // TODO: streamaus vois olla kohdillaan kun esim posti -koodistosta tulee 1,4 meganen xsd-dokkari
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<?xml version=\"1.0\"?>\n" + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
                + "           targetNamespace=\"http://service.koodisto.sade.vm.fi/types/koodisto\"\n"
                + "           xmlns=\"http://service.koodisto.sade.vm.fi/types/koodisto\"\n" + "           elementFormDefault=\"qualified\">\n" + "\n"
                + "    <xs:simpleType name=\"%s\">\n" + "        <xs:restriction base=\"xs:string\">\n", escapeXml(koodistoUri, true)));
        for (KoodistoVersioKoodiVersio koodiVersio : koodisto.getKoodiVersios()) {
            sb.append(String.format("            <xs:enumeration value=\"%s\">\n" + "                <xs:annotation>\n",
                    escapeXml(koodiVersio.getKoodiVersio().getKoodiarvo(), false)));
            for (KoodiMetadata metadata : koodiVersio.getKoodiVersio().getMetadatas()) {
                sb.append(String.format("                    <xs:documentation xml:lang=\"%s\">%s</xs:documentation>\n", metadata.getKieli().toString()
                        .toLowerCase(), escapeXml(metadata.getNimi(), false)));
            } // for koodi metadatas
            sb.append("                </xs:annotation>\n" + "            </xs:enumeration>\n" + "\n");
        } // for koodis
        sb.append("        </xs:restriction>\n" + "    </xs:simpleType>\n" + "\n" + "</xs:schema>\n");
        return sb.toString();
    }

    private String escapeXml(Object o, boolean isTypeName) {
        if (o == null) {
            return "";
        }
        String s = StringEscapeUtils.escapeXml(o.toString());
        if (isTypeName) {
            s = s.replaceAll(":", "").replaceAll("/", ""); // cannot have these chars in <xs:simpleType name="..."
        }
        return s;
    }

}
