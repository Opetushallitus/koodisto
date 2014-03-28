package fi.vm.sade.koodisto.service.business.marshaller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.ByteArrayDataSource;

/**
 * User: Turtiainen Date: 26.2.2014
 */
@Component
public class KoodistoXlsConverter extends KoodistoConverter {

    private static final KoodistoCsvConverter csvConverter = new KoodistoCsvConverter();

    private static final String[] numberFieldHeaders = { KoodistoCsvConverter.VERSIO_COLUMN };
    private static final String[] dateFieldHeaders = { KoodistoCsvConverter.PAIVITYSPVM_COLUMN, KoodistoCsvConverter.VOIMASSAALKUPVM_COLUMN,
            KoodistoCsvConverter.VOIMASSALOPPUPVM_COLUMN };

    private static final List<String> blankDocumentHeaderFields;
    protected static final String[] metadataFields = { KoodistoCsvConverter.NIMI_COLUMN, KoodistoCsvConverter.KUVAUS_COLUMN,
            KoodistoCsvConverter.LYHYTNIMI_COLUMN };

    static {
        blankDocumentHeaderFields = new LinkedList<String>();
        blankDocumentHeaderFields.add(KoodistoCsvConverter.KOODIARVO_COLUMN);
        for (KieliType kieli : KoodistoCsvConverter.kielet) {
            for (String metadataField : metadataFields) {
                blankDocumentHeaderFields.add(metadataField + "_" + kieli.name());
            }
        }
        blankDocumentHeaderFields.add(KoodistoCsvConverter.VOIMASSAALKUPVM_COLUMN);
        blankDocumentHeaderFields.add(KoodistoCsvConverter.VOIMASSALOPPUPVM_COLUMN);
    }


    @Override
    public DataHandler marshal(List<KoodiType> koodis, String encoding) throws IOException {
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
            return new DataHandler(new ByteArrayDataSource(outputStream.toByteArray()));
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private void postprocess(HSSFSheet sheet) {
        for (int i = 0; i < KoodistoCsvConverter.HEADER_FIELDS.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void writeHeader(HSSFSheet sheet) {
        HSSFRow headerRow = sheet.createRow(0);
        int iterator = 0;
        for (String fieldHeader : KoodistoCsvConverter.HEADER_FIELDS) {
            headerRow.createCell(iterator++).setCellValue(fieldHeader);
        }
    }

    private void writeCodeelements(HSSFSheet sheet, List<KoodiType> koodis) {
        int rowIterator = 1;
        for (KoodiType koodi : koodis) {
            List<String> koodiAsList = csvConverter.convert(koodi, Charset.defaultCharset()); // Huom! Käyttää KoodistoCsvConverter :ia!
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
    public List<KoodiType> unmarshal(DataHandler handler, String encoding) throws IOException {
        HSSFWorkbook workbook = null;
        workbook = new HSSFWorkbook(handler.getInputStream());
        List<KoodiType> koodis = new ArrayList<KoodiType>();
        HSSFRow firstRow = workbook.getSheetAt(0).getRow(0);
        List<String> firstRowAsList = convertRowToStringArray(firstRow);
        Map<Integer, String> fieldNameToIndex = csvConverter.createFieldNameToIndexMap(firstRowAsList);
        int rowLength = fieldNameToIndex.size();

        boolean first = true; // Skip the header row
        for (Row row : workbook.getSheetAt(0)) {
            if (first) {
                first = false;
            } else {
                List<String> rowAsCSV = convertRowToStringArray(rowLength, row, fieldNameToIndex);
                koodis.add(csvConverter.createKoodiFromCsvRow(rowAsCSV, fieldNameToIndex)); // Huom! Käyttää KoodistoCsvConverter :ia!
            }
        }

        return koodis;
    }

    private List<String> convertRowToStringArray(Row row) {
        ArrayList<String> rowAsCSV = new ArrayList<String>();
        for (int cellIterator = 0; cellIterator < row.getLastCellNum(); cellIterator++) {
            Cell cell = row.getCell(cellIterator);
            if (cell == null) { // Empty cell
                rowAsCSV.add("");
                continue;
            }
            rowAsCSV.add(cell.getStringCellValue());
        }
        return rowAsCSV;
    }

    private List<String> convertRowToStringArray(int rowLength, Row row, Map<Integer, String> fieldNameToIndex) {
        ArrayList<String> rowAsCSV = new ArrayList<String>();
        for (int cellIterator = 0; cellIterator < rowLength; cellIterator++) {
            Cell cell = row.getCell(cellIterator);
            if (cell == null) { // Empty cell
                rowAsCSV.add(null);
                continue;
            }

            // The cell can have any CELL_TYPE associated with it, so we need to know how to interpret the content.
            String headerName = fieldNameToIndex.get(cellIterator);
            if (Arrays.asList(numberFieldHeaders).contains(headerName)) { // Number (versio)
                rowAsCSV.add(cellValueAsInteger(cell));
            } else if (Arrays.asList(dateFieldHeaders).contains(headerName)) { // Date (is represented as CELL_TYPE_NUMERIC)
                rowAsCSV.add(cellValueAsDate(cell));
            } else { // String field (all other fields)
                rowAsCSV.add(cellValueAsString(cell));
            }
        }
        return rowAsCSV;
    }

    private String cellValueAsInteger(Cell cell) {
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            return cell.getStringCellValue();
        case Cell.CELL_TYPE_NUMERIC:
            return String.valueOf((int) cell.getNumericCellValue());
        default:
            return null;
        }
    }

    private String cellValueAsDate(Cell cell) {
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            return cell.getStringCellValue();
        case Cell.CELL_TYPE_NUMERIC:
            return csvConverter.convertFromDate(cell.getDateCellValue(), Charset.defaultCharset());
        default:
            return null;
        }
    }

    private String cellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            return cell.getStringCellValue();
        case Cell.CELL_TYPE_NUMERIC:
            return (String.valueOf(cell.getNumericCellValue()));
        default:
            return null;
        }
    }

    public DataHandler getBlancDocument() throws IOException {
        ByteArrayOutputStream outputStream = null;

        HSSFWorkbook book = new HSSFWorkbook();
        HSSFSheet sheet = book.createSheet("Koodisto");
        writeBlankHeader(sheet);
        postprocess(sheet);

        outputStream = new ByteArrayOutputStream();
        try {
            book.write(outputStream);
            outputStream.flush();
            return new DataHandler(new ByteArrayDataSource(outputStream.toByteArray()));
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private void writeBlankHeader(HSSFSheet sheet) {
        Row headerRow = sheet.createRow(0);
        int iterator = 0;
        for (String header : blankDocumentHeaderFields) {
            headerRow.createCell(iterator++).setCellValue(header);
        }

    }

}
