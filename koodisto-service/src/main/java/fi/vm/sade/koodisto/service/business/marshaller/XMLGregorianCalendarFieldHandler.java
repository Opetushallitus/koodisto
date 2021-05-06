package fi.vm.sade.koodisto.service.business.marshaller;

import fi.vm.sade.koodisto.util.DateHelper;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

public class XMLGregorianCalendarFieldHandler extends XmlAdapter<XMLGregorianCalendar, Date> {

    @Override
    public Date unmarshal(XMLGregorianCalendar xmlGregorianCalendar) {
        return DateHelper.xmlCalToDate(xmlGregorianCalendar);
    }

    @Override
    public XMLGregorianCalendar marshal(Date date) {
        return DateHelper.dateToXmlCal(date);
    }

}
