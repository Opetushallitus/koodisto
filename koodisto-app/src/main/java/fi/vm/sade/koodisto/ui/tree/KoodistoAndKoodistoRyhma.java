package fi.vm.sade.koodisto.ui.tree;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import fi.vm.sade.koodisto.service.types.common.KoodistoListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoVersioListType;

public class KoodistoAndKoodistoRyhma extends KoodistoListType {
    private static final long serialVersionUID = 6800553698062296333L;

    private KoodistoListType koodisto;
    private KoodistoRyhmaListType koodistoRyhma;

    public KoodistoAndKoodistoRyhma(KoodistoListType koodisto, KoodistoRyhmaListType koodistoRyhma) {
        this.koodisto = koodisto;
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
        KoodistoAndKoodistoRyhma rhs = (KoodistoAndKoodistoRyhma) other;
        return new EqualsBuilder().append(koodisto, rhs.koodisto).append(koodistoRyhma, rhs.koodistoRyhma).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(koodisto).append(koodistoRyhma).toHashCode();
    }

    @Override
    public String getKoodistoUri() {
        return koodisto.getKoodistoUri();
    }

    @Override
    public void setKoodistoUri(String value) {
        koodisto.setKoodistoUri(value);
    }

    @Override
    public String getOmistaja() {
        return koodisto.getOmistaja();
    }

    @Override
    public void setOmistaja(String value) {
        koodisto.setOmistaja(value);
    }

    @Override
    public String getOrganisaatioOid() {
        return koodisto.getOrganisaatioOid();
    }

    @Override
    public void setOrganisaatioOid(String value) {
        koodisto.setOrganisaatioOid(value);
    }

    @Override
    public Boolean isLukittu() {
        return koodisto.isLukittu();
    }

    @Override
    public void setLukittu(Boolean value) {
        koodisto.setLukittu(value);
    }

    @Override
    public KoodistoVersioListType getLatestKoodistoVersio() {
        return koodisto.getLatestKoodistoVersio();
    }

    @Override
    public void setLatestKoodistoVersio(KoodistoVersioListType value) {
        koodisto.setLatestKoodistoVersio(value);
    }

    @Override
    public List<KoodistoVersioListType> getKoodistoVersios() {
        return koodisto.getKoodistoVersios();
    }

}
