package fi.vm.sade.koodisto.service.business.changes;

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
}
