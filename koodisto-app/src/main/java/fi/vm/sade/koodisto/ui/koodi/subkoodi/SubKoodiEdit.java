package fi.vm.sade.koodisto.ui.koodi.subkoodi;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.*;
import fi.vm.sade.koodisto.ui.KoodistoApplication;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.ui.tree.AbstractKoodistoTree;
import fi.vm.sade.koodisto.util.KoodistoHelper;

import java.util.*;

@SuppressWarnings("serial")
/**
 * 
 * @author kkammone
 *
 */
public class SubKoodiEdit extends VerticalLayout {

    private static final String FULL = "100%";
    private static final String CHOOSE = "select";
    private static final String VALUE = "arvo";
    private static final String NAME = "nimi";

    private KoodiType koodiType;

    private SuhteenTyyppiType suhteenTyyppi;

    private boolean isChild;

    private Table table;
    private static final int TABLE_MAX_HEIGHT = 50;

    private AbstractKoodistoTree abstractKoodistoTree;

    /**
     * These are the koodiDTO that are selected for {@link KoodiType}
     */
    private Map<KoodiUriAndVersioType, KoodistoItemType> selectedKoodiUris = new HashMap<KoodiUriAndVersioType, KoodistoItemType>();
    /**
     * these are the existing relations
     */

    private Map<KoodiUriAndVersioType, KoodistoItemType> originalKoodiUris = new HashMap<KoodiUriAndVersioType, KoodistoItemType>();

    private CheckBox selectAllCheckbox;
    private Button saveButton;
    private Button cancelButton;
    private KoodistoPresenter presenter;

    public SubKoodiEdit(KoodistoPresenter presenter) {
        this.presenter = presenter;
        setMargin(true);
        setWidth(FULL);
        initialize();
    }

    /**
     * create layout & components for this view
     */
    private void initialize() {
        createSubLayout();
        createFooter();
    }

    public void refresh(KoodiType koodi, boolean isChild, SuhteenTyyppiType suhteenTyyppi) {
        selectedKoodiUris.clear();
        originalKoodiUris.clear();

        this.koodiType = koodi;
        this.isChild = isChild;
        this.suhteenTyyppi = suhteenTyyppi;

        List<KoodiType> koodis = presenter.listKoodiByRelation(koodiType.getKoodiUri(), koodiType.getVersio(), this.isChild, this.suhteenTyyppi);
        for (KoodiType k : koodis) {
            addKoodiToMap(k, selectedKoodiUris);
        }

        originalKoodiUris.putAll(selectedKoodiUris);
        abstractKoodistoTree.refresh();
        table.setVisible(false);
        selectAllCheckbox.setVisible(false);
    }

    private void addKoodiToMap(KoodiType k, Map<KoodiUriAndVersioType, KoodistoItemType> map) {
        String koodiUri = k.getKoodiUri();
        int versio = k.getVersio();

        KoodiUriAndVersioType kvt = new KoodiUriAndVersioType();
        kvt.setKoodiUri(koodiUri);
        kvt.setVersio(versio);

        if (!map.containsKey(kvt)) {
            map.put(kvt, k.getKoodisto());
        }
    }

    private void removeKoodiFromMap(KoodiType k, Map<KoodiUriAndVersioType, KoodistoItemType> map) {
        String koodiUri = k.getKoodiUri();
        int versio = k.getVersio();

        KoodiUriAndVersioType kvt = new KoodiUriAndVersioType();
        kvt.setKoodiUri(koodiUri);
        kvt.setVersio(versio);

        if (map.containsKey(kvt)) {
            map.remove(kvt);
        }
    }

    /**
     * creates footer (save & cancel buttons)
     * 
     * @param
     */
    private void createFooter() {
        HorizontalLayout l = new HorizontalLayout();
        saveButton = new Button(I18N.getMessage("subKoodiEdit.button.save"));
        cancelButton = new Button(I18N.getMessage("subKoodiEdit.button.cancel"));

        saveButton.addListener(new ClickListener() {

            public void buttonClick(ClickEvent event) {
                save();
            }

        });
        cancelButton.addListener(new ClickListener() {

            public void buttonClick(ClickEvent event) {
                abstractKoodistoTree.getSearchBox().setValue("");
                abstractKoodistoTree.setTextfilter(null);
                presenter.cancelSubKoodiEdit();
            }
        });

        l.addComponent(cancelButton);
        l.addComponent(saveButton);

        addComponent(l);
        setComponentAlignment(l, Alignment.BOTTOM_RIGHT);
    }

    /**
     * creates middle layout (the actual functionality in this view)
     * 
     * @param
     */
    private void createSubLayout() {
        addComponent(new Label(I18N.getMessage("subKoodiEdit.label.notification")));
        HorizontalLayout subLayout = new HorizontalLayout();
        subLayout.setWidth(FULL);
        addComponent(subLayout);
        VerticalLayout leftColumn = new VerticalLayout();
        VerticalLayout rightColumn = new VerticalLayout();
        subLayout.addComponent(leftColumn);
        subLayout.addComponent(rightColumn);
        subLayout.setExpandRatio(leftColumn, 1.0f);
        subLayout.setExpandRatio(rightColumn, 2.0f);

        this.abstractKoodistoTree = createKoodiBox();
        subLayout.addComponent(abstractKoodistoTree);
        this.table = createKoodistoSelectionBox();
        this.selectAllCheckbox = new CheckBox(I18N.getMessage("subKoodiEdit.checkbox.selectAll"));
        this.selectAllCheckbox.setVisible(false);
        this.selectAllCheckbox.setImmediate(true);
        this.selectAllCheckbox.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                for (Object obj : table.getItemIds()) {
                    Item item = table.getItem(obj);
                    Property property = item.getItemProperty(CHOOSE);
                    CheckBox checkBox = (CheckBox) property.getValue();

                    Collection<?> listeners = checkBox.getListeners(ValueChangeEvent.class);

                    // Remove checkbox listener temporarily
                    CheckBoxListener cbl = null;
                    for (Object l : listeners) {
                        if (l instanceof CheckBoxListener) {
                            cbl = (CheckBoxListener) l;
                            checkBox.removeListener(cbl);
                            break;
                        }
                    }

                    checkBox.setValue(selectAllCheckbox.booleanValue());

                    if (cbl != null) {
                        cbl.refreshSelectedKoodis();
                        checkBox.addListener(cbl);
                    }
                }
                abstractKoodistoTree.refresh();
            }
        });

        leftColumn.addComponent(this.abstractKoodistoTree);

        rightColumn.addComponent(selectAllCheckbox);
        rightColumn.addComponent(this.table);

        addComponent(subLayout);
    }

    private AbstractKoodistoTree createKoodiBox() {
        return new NestedAbrstractKoodistoTree(presenter);
    }

    private int countSelectedKoodisForKoodisto(KoodistoListType koodisto, KoodistoVersioListType koodistoVersio) {
        int counter = 0;
        for (KoodistoItemType k : selectedKoodiUris.values()) {
            if (k != null && koodisto.getKoodistoUri().equals(k.getKoodistoUri()) && k.getKoodistoVersio().contains(koodistoVersio.getVersio())) {
                ++counter;
            }
        }
        return counter;
    }

    private Table createKoodistoSelectionBox() {
        Table t = new Table();
        t.addContainerProperty(CHOOSE, CheckBox.class, null, "", null, null);
        t.addContainerProperty(VALUE, Label.class, null, I18N.getMessage("subKoodiEdit.value"), null, null);
        t.addContainerProperty(NAME, Label.class, null, I18N.getMessage("subKoodiEdit.name"), null, null);

        t.setColumnExpandRatio(NAME, 1.0f);
        t.setColumnExpandRatio(CHOOSE, -1.0f);
        t.setColumnExpandRatio(VALUE, -1.0f);
        t.setColumnWidth(CHOOSE, -1);
        t.setColumnWidth(VALUE, -1);
        t.setColumnWidth(NAME, -1);
        t.setWidth(FULL);
        t.setVisible(false);
        return t;
    }

    private void save() {

        if (!validate(selectedKoodiUris)) {
            return;
        }

        Set<KoodiUriAndVersioType> intersection = new HashSet<KoodiUriAndVersioType>(originalKoodiUris.keySet());
        intersection.retainAll(selectedKoodiUris.keySet());

        HashSet<KoodiUriAndVersioType> toRemove = new HashSet<KoodiUriAndVersioType>(originalKoodiUris.keySet());
        toRemove.removeAll(intersection);

        Set<KoodiUriAndVersioType> toAdd = new HashSet<KoodiUriAndVersioType>(selectedKoodiUris.keySet());
        toAdd.removeAll(intersection);

        KoodiUriAndVersioType thisKoodi = new KoodiUriAndVersioType();
        thisKoodi.setKoodiUri(koodiType.getKoodiUri());
        thisKoodi.setVersio(koodiType.getVersio());

        KoodiRelations koodiRelations = new KoodiRelations();

        if (this.isChild) {
            for (KoodiUriAndVersioType ylakoodi : toAdd) {
                koodiRelations.addRelationToAdd(ylakoodi, thisKoodi, suhteenTyyppi);
            }

            for (KoodiUriAndVersioType ylakoodi : toRemove) {
                koodiRelations.addRelationToRemove(ylakoodi, thisKoodi, suhteenTyyppi);
            }

        } else {
            koodiRelations.addRelationsToAdd(thisKoodi, toAdd, suhteenTyyppi);
            koodiRelations.addRelationsToRemove(thisKoodi, toRemove, suhteenTyyppi);
        }

        presenter.addAndRemoveRelations(koodiRelations);
    }

    private boolean validate(Map<KoodiUriAndVersioType, KoodistoItemType> selectedKoodiUris) {

        Set<KoodiUriAndVersioType> koodiUriAndVersioTypes = selectedKoodiUris.keySet();
        Set<String> koodisUris = new HashSet<String>();

        // Samasta koodista ei saa yrittää lisätä useampaa versiota.
        for (KoodiUriAndVersioType koodiUriAndVersioType : koodiUriAndVersioTypes) {
            String koodiUri = koodiUriAndVersioType.getKoodiUri();
            if (koodisUris.contains(koodiUri)) {
                KoodistoApplication.getInstance().getMainWindow()
                        .showNotification(I18N.getMessage("subKoodiEdit.addRelation.failed.multipleRelations"), Window.Notification.TYPE_ERROR_MESSAGE);
                return false;
            }
            koodisUris.add(koodiUri);
        }

        // Ristikkäisiä viittauksia (sisältyy, sisältää) ei saa olla.
        if (SuhteenTyyppiType.SISALTYY.equals(suhteenTyyppi)) {

            Set<String> selectedIdPairs = new HashSet<String>();

            for (KoodiUriAndVersioType koodiUriAndVersioType : koodiUriAndVersioTypes) {

                String idPair = "";
                if (this.isChild) {
                    idPair = koodiUriAndVersioType.getKoodiUri() + "#" + koodiType.getKoodiUri();
                } else {
                    idPair = koodiType.getKoodiUri() + "#" + koodiUriAndVersioType.getKoodiUri();
                }

                selectedIdPairs.add(idPair);

            }

            Set<String> storedIdPairs = new HashSet<String>();
            List<KoodiType> koodis = presenter.listKoodiByRelation(koodiType.getKoodiUri(), koodiType.getVersio(), !this.isChild, this.suhteenTyyppi);
            for (KoodiType koodi : koodis) {

                String idPair = "";
                if (this.isChild) {
                    idPair = koodi.getKoodiUri() + "#" + koodiType.getKoodiUri();
                } else {
                    idPair = koodiType.getKoodiUri() + "#" + koodi.getKoodiUri();
                }

                storedIdPairs.add(idPair);
            }

            Set<String> duplicates = new HashSet<String>();
            duplicates.addAll(selectedIdPairs);
            duplicates.retainAll(storedIdPairs);

            if (duplicates.size() > 0) {
                KoodistoApplication.getInstance().getMainWindow()
                        .showNotification(I18N.getMessage("subKoodiEdit.addRelation.failed.crossRelations"), Window.Notification.TYPE_ERROR_MESSAGE);
                return false;
            }

        }

        // List<KoodiType> koodis =
        // presenter.listKoodiByRelation(koodiType.getKoodiUri(),
        // koodiType.getVersio(), this.isChild, this.suhteenTyyppi);

        return true;

    }

    private final class NestedAbrstractKoodistoTree extends AbstractKoodistoTree {
        private NestedAbrstractKoodistoTree(KoodistoPresenter presenter) {
            super(presenter);
        }

        @Override
        protected void koodistoRyhmaClicked(KoodistoRyhmaListType koodistoRyhmaDTO) {
            // do nothing in this case

        }

        @Override
        protected void koodistoVersioClicked(KoodistoListType koodistoSimpleDTO, KoodistoVersioListType koodistoVersioDTO) {
            table.setVisible(true);
            Collection<?> listeners = selectAllCheckbox.getListeners(ValueChangeEvent.class);

            ValueChangeListener vcl = null;
            for (Object listener : listeners) {
                if (listener instanceof ValueChangeListener) {
                    vcl = (ValueChangeListener) listener;
                    selectAllCheckbox.removeListener(vcl);
                }
            }

            selectAllCheckbox.setValue(false);
            selectAllCheckbox.addListener(vcl);

            selectAllCheckbox.setVisible(true);
            table.removeAllItems();
            List<KoodiType> koodiList = presenter.listKoodisByKoodisto(koodistoSimpleDTO.getKoodistoUri(), koodistoVersioDTO.getVersio());
            KieliType locale = KoodistoHelper.getKieliForLocale(I18N.getLocale());
            for (KoodiType k : koodiList) {

                if (koodiType.getKoodiUri().equals(k.getKoodiUri())) {
                    continue;
                }

                KoodiUriAndVersioType kvt = new KoodiUriAndVersioType();
                kvt.setKoodiUri(k.getKoodiUri());
                kvt.setVersio(k.getVersio());

                CheckBox checkBox = new CheckBox();
                checkBox.setSizeUndefined();

                checkBox.setValue(selectedKoodiUris.containsKey(kvt));

                checkBox.addListener(new CheckBoxListener(checkBox, k));
                checkBox.setImmediate(true);

                String value = "N/A";
                String name = "N/A";
                if (k.getKoodiArvo() != null) {
                    value = k.getKoodiArvo();
                }
                if (k.getMetadata() != null && KoodistoHelper.getKoodiMetadataForLanguage(k, locale) != null) {
                    name = KoodistoHelper.getKoodiMetadataForLanguage(k, locale).getNimi();
                }

                Label valueLabel = new Label(value);
                Label nameLabel = new Label(name);

                valueLabel.setSizeUndefined();
                nameLabel.setSizeUndefined();

                Object[] line = new Object[] { checkBox, valueLabel, nameLabel };

                table.addItem(line, k);
            }

            table.setPageLength(Math.min(table.size(), TABLE_MAX_HEIGHT));
        }

        @Override
        protected String createNameForKoodistoVersion(KoodistoListType koodisto, KoodistoVersioListType koodistoVersio, KieliType locale) {
            KoodistoMetadataType koodistoJoukkoMetadata = KoodistoHelper.getKoodistoMetadataForLanguage(koodistoVersio, locale);
            String caption = "N/A";
            if (koodistoJoukkoMetadata != null) {
                caption = koodistoJoukkoMetadata.getNimi() + " v. " + koodistoVersio.getVersio();
            }

            int size = countSelectedKoodisForKoodisto(koodisto, koodistoVersio);
            caption += " (" + size + " " + I18N.getMessage("subKoodiEdit.chosen") + ")";
            return caption;
        }
    }

    private class CheckBoxListener implements ValueChangeListener {

        CheckBox checkBox;
        KoodiType koodi;

        CheckBoxListener(CheckBox checkBox, KoodiType koodi) {
            this.koodi = koodi;
            this.checkBox = checkBox;
        }

        public void refreshSelectedKoodis() {
            if (this.checkBox.booleanValue()) {
                addKoodiToMap(koodi, selectedKoodiUris);
            } else {
                removeKoodiFromMap(koodi, selectedKoodiUris);
            }
        }

        public void valueChange(ValueChangeEvent event) {
            refreshSelectedKoodis();
            abstractKoodistoTree.refresh();
        }
    }

    public AbstractKoodistoTree getAbstractKoodistoTree() {
        return abstractKoodistoTree;
    }

    public Table getTable() {
        return table;
    }

    public CheckBox getSelectAllCheckbox() {
        return selectAllCheckbox;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
