package fi.vm.sade.koodisto.service.business.changes;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MuutosTila {
    EI_MUUTOKSIA("Ei muutoksia"), MUUTOKSIA("Muutoksia on tapahtunut"), POISTETTU("Poistettu");
    
    private final String value;
    
    MuutosTila(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String jsonValue() {
        return name();
    }
}
