package fi.vm.sade.koodisto.service.business.marshaller;

import fi.vm.sade.koodisto.util.DateHelper;
import org.exolab.castor.mapping.GeneralizedFieldHandler;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

/**
 * User: kwuoti
 * Date: 8.4.2013
 * Time: 12.08
 */
public class XMLGregorianCalendarFieldHandler extends GeneralizedFieldHandler {

    public XMLGregorianCalendarFieldHandler() {
        super();
    }

    @Override
    public Object convertUponGet(Object value) {

        if (value == null) {
            return null;
        }

        return DateHelper.xmlCalToDate((XMLGregorianCalendar) value);
    }

    @Override
    public Object convertUponSet(Object value) {
        if (value == null) {
            return null;
        }

        return DateHelper.DateToXmlCal((Date) value);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getFieldType() {
        return XMLGregorianCalendar.class;
    }
}
