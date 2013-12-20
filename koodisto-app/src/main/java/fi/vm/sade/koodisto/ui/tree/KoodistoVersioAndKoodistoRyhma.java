package fi.vm.sade.koodisto.ui.tree;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import fi.vm.sade.koodisto.service.types.common.KoodistoListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoVersioListType;
import fi.vm.sade.koodisto.service.types.common.TilaType;

public class KoodistoVersioAndKoodistoRyhma extends KoodistoVersioListType {
    private static final long serialVersionUID = -2860924863422266313L;

    private KoodistoVersioListType koodistoVersio;
    private KoodistoRyhmaListType koodistoRyhma;

    public KoodistoVersioAndKoodistoRyhma(KoodistoVersioListType koodistoVersio, KoodistoRyhmaListType koodistoRyhma) {
        this.koodistoVersio = koodistoVersio;
        this.koodistoRyhma = koodistoRyhma;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        KoodistoVersioAndKoodistoRyhma rhs = (KoodistoVersioAndKoodistoRyhma) other;
        return new EqualsBuilder().append(koodistoVersio, rhs.koodistoVersio).append(koodistoRyhma, rhs.koodistoRyhma).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(koodistoVersio).append(koodistoRyhma).toHashCode();
    }

    @Override
    public String getKoodistoUri() {
        return koodistoVersio.getKoodistoUri();
    }

    @Override
    public void setKoodistoUri(String value) {
        koodistoVersio.setKoodistoUri(value);
    }

    @Override
    public int getVersio() {
        return koodistoVersio.getVersio();
    }

    @Override
    public void setVersio(int value) {
        koodistoVersio.setVersio(value);
    }

    @Override
    public XMLGregorianCalendar getPaivitysPvm() {
        return koodistoVersio.getPaivitysPvm();
    }

    @Override
    public void setPaivitysPvm(XMLGregorianCalendar value) {
        koodistoVersio.setPaivitysPvm(value);
    }

    @Override
    public XMLGregorianCalendar getVoimassaAlkuPvm() {
        return koodistoVersio.getVoimassaAlkuPvm();
    }

    @Override
    public void setVoimassaAlkuPvm(XMLGregorianCalendar value) {
        koodistoVersio.setVoimassaAlkuPvm(value);
    }

    @Override
    public XMLGregorianCalendar getVoimassaLoppuPvm() {
        return koodistoVersio.getVoimassaLoppuPvm();
    }

    @Override
    public void setVoimassaLoppuPvm(XMLGregorianCalendar value) {
        koodistoVersio.setVoimassaLoppuPvm(value);
    }

    @Override
    public TilaType getTila() {
        return koodistoVersio.getTila();
    }

    @Override
    public void setTila(TilaType value) {
        koodistoVersio.setTila(value);
    }

    @Override
    public List<KoodistoMetadataType> getMetadataList() {
        return koodistoVersio.getMetadataList();
    }

    @Override
    public KoodistoListType getKoodisto() {
        return koodistoVersio.getKoodisto();
    }

    @Override
    public void setKoodisto(KoodistoListType value) {
        koodistoVersio.setKoodisto(value);
    }

}
