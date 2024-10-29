package fi.vm.sade.koodisto.service.types;

public class SearchKoodisCriteriaType
    extends KoodiBaseSearchCriteriaType
{

    private static final long serialVersionUID = 1L;
    protected Integer koodiVersio;

    protected SearchKoodisVersioSelectionType koodiVersioSelection;

    public Integer getKoodiVersio() {
        return koodiVersio;
    }

    public void setKoodiVersio(Integer value) {
        this.koodiVersio = value;
    }

    public SearchKoodisVersioSelectionType getKoodiVersioSelection() {
        return koodiVersioSelection;
    }

    public void setKoodiVersioSelection(SearchKoodisVersioSelectionType value) {
        this.koodiVersioSelection = value;
    }

}
