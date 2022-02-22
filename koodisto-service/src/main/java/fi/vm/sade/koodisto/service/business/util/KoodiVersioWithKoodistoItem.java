package fi.vm.sade.koodisto.service.business.util;

import fi.vm.sade.koodisto.model.KoodiVersio;

public class KoodiVersioWithKoodistoItem {
    private KoodiVersio koodiVersio;
    private KoodistoItem koodistoItem;

    public KoodiVersioWithKoodistoItem() {

    }

    public KoodiVersioWithKoodistoItem(KoodiVersio koodiVersio, KoodistoItem koodistoItem) {
        this.koodiVersio = koodiVersio;
        this.koodistoItem = koodistoItem;
    }

    public KoodiVersio getKoodiVersio() {
        return koodiVersio;
    }

    public void setKoodiVersio(KoodiVersio koodiVersio) {
        this.koodiVersio = koodiVersio;
    }

    public KoodistoItem getKoodistoItem() {
        return koodistoItem;
    }

    public void setKoodistoItem(KoodistoItem koodistoItem) {
        this.koodistoItem = koodistoItem;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((koodiVersio == null) ? 0 : koodiVersio.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        KoodiVersioWithKoodistoItem other = (KoodiVersioWithKoodistoItem) obj;
        if (koodiVersio == null) {
            if (other.koodiVersio != null) {
                return false;
            }
        } else if (!koodiVersio.equals(other.koodiVersio)) {
            return false;
        }
        return true;
    }
}
