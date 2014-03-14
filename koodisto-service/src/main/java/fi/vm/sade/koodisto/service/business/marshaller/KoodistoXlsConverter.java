package fi.vm.sade.koodisto.service.business.marshaller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.ByteArrayDataSource;

/**
 * User: Turtiainen Date: 26.2.2014
 */
@Component
public class KoodistoXlsConverter extends KoodistoConverter {

	public static final SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.S");

	public static final List<String> HEADER_FIELDS;
	public static final KoodistoCsvConverter csvConverter = new KoodistoCsvConverter();

	private static final String VERSIO_COLUMN = "VERSIO";
	private static final String KOODIURI_COLUMN = "KOODIURI";
	private static final String KOODIARVO_COLUMN = "KOODIARVO";
	private static final String PAIVITYSPVM_COLUMN = "PAIVITYSPVM";
	private static final String VOIMASSAALKUPVM_COLUMN = "VOIMASSAALKUPVM";
	private static final String VOIMASSALOPPUPVM_COLUMN = "VOIMASSALOPPUPVM";
	private static final String TILA_COLUMN = "TILA";

	private static final String NIMI_COLUMN = "NIMI";
	private static final String KUVAUS_COLUMN = "KUVAUS";
	private static final String LYHYTNIMI_COLUMN = "LYHYTNIMI";
	private static final String KAYTTOOHJE_COLUMN = "KAYTTOOHJE";
	private static final String KASITE_COLUMN = "KASITE";
	private static final String SISALTAAMERKITYKSEN_COLUMN = "SISALTAAMERKITYKSEN";
	private static final String EISISALLAMERKITYSTA_COLUMN = "EISISALLAMERKITYSTA";
	private static final String HUOMIOITAVAKOODI_COLUMN = "HUOMIOITAVAKOODI";
	private static final String SISALTAAKOODISTON_COLUMN = "SISALTAAKOODISTON";

	static {
		String[] basicFields = { VERSIO_COLUMN, KOODIURI_COLUMN,
				KOODIARVO_COLUMN, PAIVITYSPVM_COLUMN, VOIMASSAALKUPVM_COLUMN,
				VOIMASSALOPPUPVM_COLUMN, TILA_COLUMN };

		String[] metadataFields = { NIMI_COLUMN, KUVAUS_COLUMN,
				LYHYTNIMI_COLUMN, KAYTTOOHJE_COLUMN, KASITE_COLUMN,
				SISALTAAMERKITYKSEN_COLUMN, EISISALLAMERKITYSTA_COLUMN,
				HUOMIOITAVAKOODI_COLUMN, SISALTAAKOODISTON_COLUMN };

		KieliType[] kielet = { KieliType.FI, KieliType.SV, KieliType.EN };

		HEADER_FIELDS = new LinkedList<String>(Arrays.asList(basicFields));
		for (KieliType kieli : kielet) {
			for (String metadataField : metadataFields) {
				HEADER_FIELDS.add(metadataField + "_" + kieli.name());
			}
		}
	}

	@Override
	public DataHandler marshal(List<KoodiType> koodis, String encoding)
			throws IOException {
		ByteArrayOutputStream outputStream = null;

		HSSFWorkbook book = new HSSFWorkbook();
		HSSFSheet sheet = book.createSheet("Koodisto");
		writeHeader(sheet);
		writeCodeelements(sheet, koodis);
		postprocess(sheet);

		outputStream = new ByteArrayOutputStream();
		try {
			book.write(outputStream);
			outputStream.flush();
			return new DataHandler(new ByteArrayDataSource(
					outputStream.toByteArray()));
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	private void postprocess(HSSFSheet sheet) {
		for (int i = 0; i < HEADER_FIELDS.size(); i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private void writeHeader(HSSFSheet sheet) {
		HSSFRow headerRow = sheet.createRow(0);
		int iterator = 0;
		for (String fieldHeader : HEADER_FIELDS) {
			headerRow.createCell(iterator++).setCellValue(fieldHeader);
		}
	}

	private void writeCodeelements(HSSFSheet sheet, List<KoodiType> koodis) {
		int rowIterator = 1;
		for (KoodiType koodi : koodis) {
			List<String> koodiAsList = csvConverter.convert(koodi,
					Charset.defaultCharset()); // Huom! Käyttää
												// KoodistoCsvConverter :ia!
			int cellIterator = 0;
			HSSFRow row = sheet.createRow(rowIterator++);
			for (String field : koodiAsList) {
				HSSFCell cell = row.createCell(cellIterator++);
				cell.setCellValue(field);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			}
		}
	}

	@Override
	public List<KoodiType> unmarshal(DataHandler handler, String encoding)
			throws IOException {
		// HSSFWorkbook workbook = null;
		// workbook = new HSSFWorkbook(handler.getInputStream());
		// System.out.println(workbook.getSheetAt(0).getRow(0).getCell(0));
		// System.out.println(workbook.getSheetAt(0).getRow(0).getCell(1));
		// System.out.println(workbook.getSheetAt(0).getRow(0).getCell(2));
		// System.out.println(workbook.getSheetAt(0).getRow(0).getCell(3));

		// try {
		// reader = new BufferedReader(new
		// InputStreamReader(handler.getInputStream(), getCharset(encoding)));
		// csvReader = new CsvListReader(reader,
		// CsvPreference.STANDARD_PREFERENCE);
		// List<String> row = null;
		//
		// Map<Integer, String> fieldNameToIndex = createFieldNameToIndexMap();
		//
		// List<KoodiType> koodis = new ArrayList<KoodiType>();
		//
		// boolean first = true;
		// while ((row = csvReader.read()) != null) {
		// if (first) { // Skip header first = false; } else {
		// koodis.add(createKoodiFromCsvRow(row, fieldNameToIndex));
		// }
		// }
		//
		// return koodis;
		//
		// } finally {
		// if (reader != null) {
		// reader.close();
		// }
		//
		// if (csvReader != null) {
		// csvReader.close();
		// }
		// }

		return null;
	}
}
