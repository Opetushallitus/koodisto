package fi.vm.sade.koodisto.service.business.marshaller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.service.business.exception.InvalidKoodiCsvLineException;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.ByteArrayDataSource;
import fi.vm.sade.koodisto.util.KoodistoHelper;

/**
 * User: kwuoti Date: 8.4.2013 Time: 15.23
 */
@Component
public class KoodistoCsvConverter extends KoodistoConverter {

    private static final int UTF8_BYTE_ORDER_239 = 239;
    private static final int UTF8_BYTE_ORDER_187 = 187;
    private static final int UTF8_BYTE_ORDER_191 = 191;

    public static final SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    public static final List<String> HEADER_FIELDS;

    protected static final String VERSIO_COLUMN = "VERSIO";
    protected static final String KOODIURI_COLUMN = "KOODIURI";
    protected static final String KOODIARVO_COLUMN = "KOODIARVO";
    protected static final String PAIVITYSPVM_COLUMN = "PAIVITYSPVM";
    protected static final String VOIMASSAALKUPVM_COLUMN = "VOIMASSAALKUPVM";
    protected static final String VOIMASSALOPPUPVM_COLUMN = "VOIMASSALOPPUPVM";
    protected static final String TILA_COLUMN = "TILA";

    protected static final String NIMI_COLUMN = "NIMI";
    protected static final String KUVAUS_COLUMN = "KUVAUS";
    protected static final String LYHYTNIMI_COLUMN = "LYHYTNIMI";
    protected static final String KAYTTOOHJE_COLUMN = "KAYTTOOHJE";
    protected static final String KASITE_COLUMN = "KASITE";
    protected static final String SISALTAAMERKITYKSEN_COLUMN = "SISALTAAMERKITYKSEN";
    protected static final String EISISALLAMERKITYSTA_COLUMN = "EISISALLAMERKITYSTA";
    protected static final String HUOMIOITAVAKOODI_COLUMN = "HUOMIOITAVAKOODI";
    protected static final String SISALTAAKOODISTON_COLUMN = "SISALTAAKOODISTON";

    protected static final String[] basicFields = { VERSIO_COLUMN, KOODIURI_COLUMN, KOODIARVO_COLUMN, PAIVITYSPVM_COLUMN, VOIMASSAALKUPVM_COLUMN,
            VOIMASSALOPPUPVM_COLUMN, TILA_COLUMN };

    protected static final String[] metadataFields = { NIMI_COLUMN, KUVAUS_COLUMN, LYHYTNIMI_COLUMN, KAYTTOOHJE_COLUMN, KASITE_COLUMN,
            SISALTAAMERKITYKSEN_COLUMN, EISISALLAMERKITYSTA_COLUMN, HUOMIOITAVAKOODI_COLUMN, SISALTAAKOODISTON_COLUMN };

    protected static final KieliType[] kielet = { KieliType.FI, KieliType.SV, KieliType.EN };

    static {

        HEADER_FIELDS = new LinkedList<String>(Arrays.asList(basicFields));
        for (KieliType kieli : kielet) {
            for (String metadataField : metadataFields) {
                HEADER_FIELDS.add(metadataField + "_" + kieli.name());
            }
        }
    }

    protected Map<Integer, String> createFieldNameToIndexMap() {
        Map<Integer, String> map = new HashMap<Integer, String>();

        for (int i = 0; i < HEADER_FIELDS.size(); ++i) {
            map.put(i, HEADER_FIELDS.get(i));
        }

        return map;
    }

    protected Map<Integer, String> createFieldNameToIndexMap(List<String> row) { // Use the header row instead of predefined array
        Map<Integer, String> map = new HashMap<Integer, String>();
        int rowLenght = row.size();
        String[] headerFields = row.toArray(new String[rowLenght]);
        for (int i = 0; i < rowLenght; i++) {
            String header = headerFields[i];
            if (checkFieldHeaderValid(header)) {
                if (!Character.isLetter(header.charAt(0))) { // Removing BOM
                    header = header.substring(1);
                }
                map.put(i, header);
            }
        }

        return map;
    }

    private boolean checkFieldHeaderValid(String header) {
        if (StringUtils.isBlank(header))
            return false;
        if (!Character.isLetter(header.charAt(0))) { // Removing BOM
            header = header.substring(1);
        }
        if (Arrays.asList(basicFields).contains(header)) { // Basic field
            return true;
        } else { // Meta fields
            String[] splitHeader = header.split("_");
            String[] kieliNames = { KieliType.FI.name(), KieliType.SV.name(), KieliType.EN.name() };
            if (Arrays.asList(metadataFields).contains(splitHeader[0]) && Arrays.asList(kieliNames).contains(splitHeader[1])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DataHandler marshal(List<KoodiType> koodis, String encoding) throws IOException {
        CsvListWriter csvWriter = null;
        ByteArrayOutputStream outputStream = null;
        BufferedWriter writer = null;

        try {
            outputStream = new ByteArrayOutputStream();
            Charset charset = getCharset(encoding);

            if (charset.equals(getCharset("UTF-8"))) {
                outputStream.write(UTF8_BYTE_ORDER_239);
                outputStream.write(UTF8_BYTE_ORDER_187);
                outputStream.write(UTF8_BYTE_ORDER_191);
            }

            writer = new BufferedWriter(new OutputStreamWriter(outputStream, charset));
            csvWriter = new CsvListWriter(writer, CsvPreference.STANDARD_PREFERENCE);
            writeKoodis(koodis, charset, csvWriter);
            csvWriter.flush();
            writer.flush();
            outputStream.flush();

            return new DataHandler(new ByteArrayDataSource(outputStream.toByteArray()));
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }

            if (writer != null) {
                writer.close();
            }

            if (csvWriter != null) {
                csvWriter.close();
            }
        }
    }

    @Override
    public List<KoodiType> unmarshal(DataHandler handler, String encoding) throws IOException {

        BufferedReader reader = null;
        CsvListReader csvReader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(handler.getInputStream(), getCharset(encoding)));
            csvReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);
            List<String> row = null;

            Map<Integer, String> fieldNameToIndex = createFieldNameToIndexMap();

            List<KoodiType> koodis = new ArrayList<KoodiType>();

            boolean first = true;
            while ((row = csvReader.read()) != null) {
                if (first) { // Skip header
                    fieldNameToIndex = createFieldNameToIndexMap(row);
                    first = false;
                } else {
                    koodis.add(createKoodiFromCsvRow(row, fieldNameToIndex));
                }
            }

            return koodis;

        } finally {
            if (reader != null) {
                reader.close();
            }

            if (csvReader != null) {
                csvReader.close();
            }
        }
    }

    protected KoodiType createKoodiFromCsvRow(List<String> row, Map<Integer, String> fieldNameToIndex) {
        if (fieldNameToIndex.size() != row.size()) {
            throw new InvalidKoodiCsvLineException("Invalid number of fields for koodi CSV line. Required " + fieldNameToIndex.size() + " but got "
                    + row.size());
        }

        KoodiType koodi = new KoodiType();

        KoodiMetadataType fiMeta = new KoodiMetadataType();
        fiMeta.setKieli(KieliType.FI);

        KoodiMetadataType svMeta = new KoodiMetadataType();
        svMeta.setKieli(KieliType.SV);

        KoodiMetadataType enMeta = new KoodiMetadataType();
        enMeta.setKieli(KieliType.EN);

        boolean populateFiMeta = false;
        boolean populateSvMeta = false;
        boolean populateEnMeta = false;

        for (int i = 0; i < row.size(); ++i) {
            String value = row.get(i);

            String fieldName = fieldNameToIndex.get(i);

            populateKoodiFields(koodi, fieldName, value);

            if (fieldName.endsWith("_" + KieliType.FI.name()) && StringUtils.isNotBlank(value)) {
                populateMetadataField(fieldName, value, fiMeta);
                populateFiMeta = true;
            } else if (fieldName.endsWith("_" + KieliType.SV.name()) && StringUtils.isNotBlank(value)) {
                populateMetadataField(fieldName, value, svMeta);
                populateSvMeta = true;
            } else if (fieldName.endsWith("_" + KieliType.EN.name()) && StringUtils.isNotBlank(value)) {
                populateMetadataField(fieldName, value, enMeta);
                populateEnMeta = true;
            }
        }

        if (populateFiMeta) {
            koodi.getMetadata().add(fiMeta);
        }

        if (populateSvMeta) {
            koodi.getMetadata().add(svMeta);
        }

        if (populateEnMeta) {
            koodi.getMetadata().add(enMeta);
        }

        return koodi;
    }

    private void populateKoodiFields(KoodiType koodi, String fieldName, String value) {
        if (VERSIO_COLUMN.equals(fieldName) && StringUtils.isNotBlank(value)) {
            koodi.setVersio(Integer.parseInt(value));
        } else if (KOODIURI_COLUMN.equals(fieldName)) {
            koodi.setKoodiUri(value);
        } else if (KOODIARVO_COLUMN.equals(fieldName)) {
            koodi.setKoodiArvo(value);
        } else if (PAIVITYSPVM_COLUMN.equals(fieldName)) {
            koodi.setPaivitysPvm(parseDate(value));
        } else if (VOIMASSAALKUPVM_COLUMN.equals(fieldName)) {
            koodi.setVoimassaAlkuPvm(parseDate(value));
        } else if (VOIMASSALOPPUPVM_COLUMN.equals(fieldName)) {
            koodi.setVoimassaLoppuPvm(parseDate(value));
        } else if (TILA_COLUMN.equals(fieldName)) {
            koodi.setTila(TilaType.valueOf(value));
        }
    }

    private void populateMetadataField(String fieldName, String value, KoodiMetadataType metadata) {
        if (fieldName.startsWith(NIMI_COLUMN)) {
            metadata.setNimi(value);
        } else if (fieldName.startsWith(KUVAUS_COLUMN)) {
            metadata.setKuvaus(value);
        } else if (fieldName.startsWith(LYHYTNIMI_COLUMN)) {
            metadata.setLyhytNimi(value);
        } else if (fieldName.startsWith(KAYTTOOHJE_COLUMN)) {
            metadata.setKayttoohje(value);
        } else if (fieldName.startsWith(KASITE_COLUMN)) {
            metadata.setKasite(value);
        } else if (fieldName.startsWith(SISALTAAMERKITYKSEN_COLUMN)) {
            metadata.setSisaltaaMerkityksen(value);
        } else if (fieldName.startsWith(EISISALLAMERKITYSTA_COLUMN)) {
            metadata.setEiSisallaMerkitysta(value);
        } else if (fieldName.startsWith(HUOMIOITAVAKOODI_COLUMN)) {
            metadata.setHuomioitavaKoodi(value);
        } else if (fieldName.startsWith(SISALTAAKOODISTON_COLUMN)) {
            metadata.setSisaltaaKoodiston(value);
        }
    }

    private XMLGregorianCalendar parseDate(String paivamaaraString) {
        XMLGregorianCalendar date = null;
        if (paivamaaraString == null) {
            return null;
        }
        try {
            date = DateHelper.DateToXmlCal(CSV_DATE_FORMAT.parse(paivamaaraString));
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    protected void writeKoodis(List<KoodiType> koodis, Charset encoding, CsvListWriter writer) throws IOException {
        writer.write(HEADER_FIELDS);

        for (KoodiType koodi : koodis) {
            List<String> converted = convert(koodi, encoding);
            writer.write(converted);
        }
    }

    protected List<String> convert(KoodiType koodi, Charset encoding) {
        List<String> list = new ArrayList<String>();
        list.add(convertFromInteger(koodi.getVersio(), encoding));
        list.add(convertFromString(koodi.getKoodiUri(), encoding));
        list.add(convertFromString(koodi.getKoodiArvo(), encoding));
        list.add(convertFromDate(DateHelper.xmlCalToDate(koodi.getPaivitysPvm()), encoding));
        list.add(convertFromDate(DateHelper.xmlCalToDate(koodi.getVoimassaAlkuPvm()), encoding));
        list.add(convertFromDate(DateHelper.xmlCalToDate(koodi.getVoimassaLoppuPvm()), encoding));
        list.add(koodi.getTila().name());

        KoodiMetadataType fiMetadata = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KieliType.FI);
        // Check that language really matches
        if (fiMetadata != null && !KieliType.FI.equals(fiMetadata.getKieli())) {
            fiMetadata = null;
        }
        list.addAll(convert(fiMetadata, encoding));
        KoodiMetadataType svMetadata = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KieliType.SV);
        if (svMetadata != null && !KieliType.SV.equals(svMetadata.getKieli())) {
            svMetadata = null;
        }
        list.addAll(convert(svMetadata, encoding));
        KoodiMetadataType enMetadata = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KieliType.EN);
        if (enMetadata != null && !KieliType.EN.equals(enMetadata.getKieli())) {
            enMetadata = null;
        }
        list.addAll(convert(enMetadata, encoding));

        return list;
    }

    protected List<String> convert(KoodiMetadataType metadata, Charset encoding) {
        List<String> list = new ArrayList<String>();
        if (metadata == null) {
            metadata = new KoodiMetadataType();
        }

        list.add(convertFromString(metadata.getNimi(), encoding));
        list.add(convertFromString(metadata.getKuvaus(), encoding));
        list.add(convertFromString(metadata.getLyhytNimi(), encoding));
        list.add(convertFromString(metadata.getKayttoohje(), encoding));
        list.add(convertFromString(metadata.getKasite(), encoding));
        list.add(convertFromString(metadata.getSisaltaaMerkityksen(), encoding));
        list.add(convertFromString(metadata.getEiSisallaMerkitysta(), encoding));
        list.add(convertFromString(metadata.getHuomioitavaKoodi(), encoding));
        list.add(convertFromString(metadata.getSisaltaaKoodiston(), encoding));

        return list;
    }

    protected String convertFromString(String str, Charset encoding) {
        if (str == null) {
            return "";
        } else {
            String convertedString = new String(str.getBytes(), UTF8ENCODING);
            return new String(convertedString.getBytes(), encoding);
        }
    }

    protected String convertFromInteger(Integer integer, Charset encoding) {
        if (integer == null) {
            return "";
        } else {
            String convertedString = new String(String.valueOf(integer).getBytes(), UTF8ENCODING);
            return new String(convertedString.getBytes(), encoding);
        }
    }

    protected String convertFromDate(Date date, Charset encoding) {
        if (date == null) {
            return "";
        } else {
            String convertedString = new String(CSV_DATE_FORMAT.format(date).getBytes(), UTF8ENCODING);
            return new String(convertedString.getBytes(), encoding);
        }
    }
}
