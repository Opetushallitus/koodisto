/**
 * 
 */
package fi.vm.sade.koodisto.ui.tree;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.CollapseListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.*;
import fi.vm.sade.koodisto.ui.KoodistoApplication;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.ui.util.CaseInsensitiveItemSorter;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.vaadin.Oph;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 
 * @author kkammone
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractKoodistoTree extends VerticalLayout {

    private static final String PASSIVE = "passive";
    private static final String PLANNED = "planned";
    private static final String OWN = "own";
    private static final String FULL = "100%";
    private static final String CAPTION = "caption";
    private static final int GRID_WIDTH = 3;
    private static final int GRID_HEIGHT = 5;

    private SimpleStringFilter textfilter = null;

    private Compare.Equal showOwnfilter = null;
    private Compare.Equal showPassivefilter = null;
    private Compare.Equal showPlannedfilter = null;

    private Tree koodistoTree;
    private HierarchicalContainer rootContainer;

    private TextField searchBox;
    private CheckBox showOwn;
    private CheckBox showPlanned;
    private CheckBox showPassive;

    private Label resultCount;

    private Object selectedItemId;

    private KoodistoRyhmaListType firstKoodistoRyhma;
    private KoodistoPresenter presenter;

    public AbstractKoodistoTree(KoodistoPresenter presenter) {
        this.presenter = presenter;
        setWidth(FULL);
        rootContainer = new HierarchicalContainer();
        rootContainer.setItemSorter(new CaseInsensitiveItemSorter());

        koodistoTree = new Tree(I18N.getMessage("koodistoTree.title"), rootContainer);
        koodistoTree.setItemCaptionPropertyId(CAPTION);
        koodistoTree.setItemDescriptionGenerator(new AbstractSelect.ItemDescriptionGenerator() {
            @Override
            public String generateDescription(Component source, Object itemId, Object propertyId) {
                KieliType kieli = KoodistoHelper.getKieliForLocale(I18N.getLocale());
                if (itemId.getClass().isAssignableFrom(KoodistoAndKoodistoRyhma.class)) {
                    KoodistoAndKoodistoRyhma koodisto = (KoodistoAndKoodistoRyhma) itemId;
                    return createNameForKoodisto(koodisto, kieli);
                } else if (itemId.getClass().isAssignableFrom(KoodistoVersioAndKoodistoRyhma.class)) {
                    KoodistoVersioAndKoodistoRyhma versio = (KoodistoVersioAndKoodistoRyhma) itemId;
                    return createNameForKoodistoVersion(versio.getKoodisto(), versio, kieli);
                } else if (itemId.getClass().isAssignableFrom(KoodistoRyhmaListType.class)) {
                    KoodistoRyhmaListType ryhma = (KoodistoRyhmaListType) itemId;
                    return createNameForKoodistoRyhma(ryhma, kieli);
                }
                return null;
            }
        });

        koodistoTree.setImmediate(true);

        KoodistoTreeEventListener eventListener = new KoodistoTreeEventListener();
        koodistoTree.addListener((ValueChangeListener) eventListener);
        koodistoTree.addListener((ExpandListener) eventListener);
        koodistoTree.addListener((CollapseListener) eventListener);

        resultCount = new Label();

        refresh();

        addComponent(createControls());
        addComponent(koodistoTree);

        koodistoTree.expandItem(firstKoodistoRyhma);
    }

    private Component createControls() {

        GridLayout layout = new GridLayout(GRID_WIDTH, GRID_HEIGHT);
        layout.setWidth(FULL);
        layout.setSpacing(true);
        layout.setMargin(true, false, true, false);
        searchBox = new TextField(I18N.getMessage("abstractKoodistoTree.search"));
        searchBox.setStyleName(Oph.TEXTFIELD_SEARCH);

        showOwn = new CheckBox(I18N.getMessage("abstractKoodistoTree.ownOnly"));
        showPlanned = new CheckBox(I18N.getMessage("abstractKoodistoTree.plannedOnly"));
        showPassive = new CheckBox(I18N.getMessage("abstractKoodistoTree.passiveOnly"));

        searchBox.setImmediate(true);
        showOwn.setImmediate(true);
        showPlanned.setImmediate(true);
        showPassive.setImmediate(true);

        searchBox.addListener(new TextChangeListener() {
            public void textChange(TextChangeEvent event) {
                textfilter = new SimpleStringFilter(CAPTION, event.getText(), true, false);
                applyFilters();
            }
        });

        showOwn.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                showOwnfilter = null;
                Boolean value = (Boolean) event.getButton().getValue();
                if (value != null && value) {
                    showOwnfilter = new Compare.Equal(OWN, false);
                }
                applyFilters();
            }
        });

        showPlanned.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                showPlannedfilter = null;
                Boolean value = (Boolean) event.getButton().getValue();
                if (value != null && !value) {
                    showPlannedfilter = new Compare.Equal(PLANNED, false);
                }
                applyFilters();
            }
        });

        showPassive.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                showPassivefilter = null;
                Boolean value = (Boolean) event.getButton().getValue();
                if (value != null && !value) {
                    showPassivefilter = new Compare.Equal(PASSIVE, false);
                }
                applyFilters();
            }
        });

        layout.addComponent(searchBox, 0, 0, 2, 0);
        layout.addComponent(showOwn, 0, 1);
        layout.addComponent(showPassive, 0, 2);
        layout.addComponent(showPlanned, 0, 3);
        layout.addComponent(resultCount, 0, 4, GRID_WIDTH - 1, 4);
        return layout;
    }

    private void applyFilters() {
        rootContainer.removeAllContainerFilters();
        applyFilterIfNotNull(rootContainer, this.textfilter);
        applyFilterIfNotNull(rootContainer, this.showOwnfilter);
        applyFilterIfNotNull(rootContainer, this.showPassivefilter);
        applyFilterIfNotNull(rootContainer, this.showPlannedfilter);
        updateResultCount();
    }

    private void updateResultCount() {
        Collection<?> visibleItems = rootContainer.getItemIds();
        if (visibleItems.size() == 0) {
            resultCount.setValue(I18N.getMessage("abstractKoodistoTree.noResults"));
        } else {
            int koodistos = 0;
            for (Object itemId : visibleItems) {
                if (itemId instanceof KoodistoAndKoodistoRyhma) {
                    ++koodistos;
                }
            }
            String resultsFound = null;
            if (koodistos == 1) {
                resultsFound = I18N.getMessage("abstractKoodistoTree.oneResult");
            } else {
                resultsFound = I18N.getMessage("abstractKoodistoTree.manyResults", koodistos);
            }
            resultCount.setValue(resultsFound);
        }
    }

    private void removeFiltersFilters() {
        rootContainer.removeAllContainerFilters();
    }

    private void applyFilterIfNotNull(HierarchicalContainer container, Filter filter) {
        if (filter != null) {
            container.addContainerFilter(filter);
        }
    }

    public void refresh() {

        removeFiltersFilters();

        rootContainer.removeAllItems();
        rootContainer.addContainerProperty(CAPTION, String.class, "");
        rootContainer.addContainerProperty(OWN, Boolean.class, false);
        rootContainer.addContainerProperty(PLANNED, Boolean.class, false);
        rootContainer.addContainerProperty(PASSIVE, Boolean.class, false);

        final KieliType kieli = KoodistoHelper.getKieliForLocale(I18N.getLocale());
        List<KoodistoRyhmaListType> koodistoList = presenter.listKoodistoRyhmas();
        Collections.sort(koodistoList, new Comparator<KoodistoRyhmaListType>() {

            @Override
            public int compare(KoodistoRyhmaListType o1, KoodistoRyhmaListType o2) {
                return createNameForKoodistoRyhma(o1, kieli).compareTo(createNameForKoodistoRyhma(o2, kieli));
            }
        });

        if (koodistoList.size() > 0) {
            firstKoodistoRyhma = koodistoList.get(0);
        }

        for (KoodistoRyhmaListType koodistoJoukko : koodistoList) {
            Item koodistoJoukkoItem = rootContainer.addItem(koodistoJoukko);
            rootContainer.setChildrenAllowed(koodistoJoukko, true);
            String caption = createNameForKoodistoRyhma(koodistoJoukko, kieli);
            koodistoJoukkoItem.getItemProperty(CAPTION).setValue(caption);

            List<KoodistoListType> koodistos = koodistoJoukko.getKoodistos();
            Collections.sort(koodistos, new Comparator<KoodistoListType>() {

                @Override
                public int compare(KoodistoListType o1, KoodistoListType o2) {
                    String koodistoNimi1 = createNameForKoodisto(o1, kieli);
                    String koodistoNimi2 = createNameForKoodisto(o2, kieli);
                    return koodistoNimi1.compareTo(koodistoNimi2);
                }
            });
            for (KoodistoListType koodisto : koodistos) {
                KoodistoAndKoodistoRyhma koodistoAndRyhma = new KoodistoAndKoodistoRyhma(koodisto, koodistoJoukko);
                addKoodistoToKoodistoTree(kieli, koodistoJoukko, koodistoAndRyhma);
            }
        }

        applyFilters();
    }

    private void addKoodistoToKoodistoTree(KieliType kieli, KoodistoRyhmaListType koodistoJoukko, KoodistoAndKoodistoRyhma koodisto) {
        Item koodistoItem = rootContainer.addItem(koodisto);
        rootContainer.setParent(koodisto, koodistoJoukko);
        rootContainer.setChildrenAllowed(koodisto, true);

        List<KoodistoVersioListType> versios = koodisto.getKoodistoVersios();
        Collections.sort(versios, new Comparator<KoodistoVersioListType>() {
            @Override
            public int compare(KoodistoVersioListType o1, KoodistoVersioListType o2) {
                return o2.getVersio() - o1.getVersio();
            }
        });

        String subCaption = createNameForKoodisto(koodisto, kieli);

        boolean owned = checkIfOwn(koodisto);
        KoodistoVersioListType latestVersio = koodisto.getLatestKoodistoVersio();

        boolean planned = false;
        boolean passive = false;
        if (latestVersio != null) {
            planned = checkIfPlanned(DateHelper.xmlCalToDate(latestVersio.getVoimassaAlkuPvm()));
            passive = checkIfPassive(latestVersio.getTila());
        }

        koodistoItem.getItemProperty(CAPTION).setValue(subCaption);
        koodistoItem.getItemProperty(OWN).setValue(owned);
        koodistoItem.getItemProperty(PLANNED).setValue(planned);
        koodistoItem.getItemProperty(PASSIVE).setValue(passive);

        for (KoodistoVersioListType koodistoVersio : versios) {
            KoodistoVersioAndKoodistoRyhma wrappedKoodistoVersio = new KoodistoVersioAndKoodistoRyhma(koodistoVersio, koodistoJoukko);
            addKoodistoVersionToKoodistoTree(koodisto, wrappedKoodistoVersio, kieli, owned);
        }
    }

    private void addKoodistoVersionToKoodistoTree(KoodistoAndKoodistoRyhma koodisto, KoodistoVersioAndKoodistoRyhma koodistoVersio, KieliType kieli,
            boolean owned) {
        koodistoVersio.setKoodisto(koodisto);
        Item koodistoVersioItem = rootContainer.addItem(koodistoVersio);
        if (koodistoVersioItem == null) {
            return;
        }
        rootContainer.setParent(koodistoVersio, koodisto);
        rootContainer.setChildrenAllowed(koodistoVersio, false);
        String versionCaption = createNameForKoodistoVersion(koodisto, koodistoVersio, kieli);

        boolean planned = checkIfPlanned(DateHelper.xmlCalToDate(koodistoVersio.getVoimassaAlkuPvm()));
        boolean passive = checkIfPassive(koodistoVersio.getTila());

        koodistoVersioItem.getItemProperty(CAPTION).setValue(versionCaption);
        koodistoVersioItem.getItemProperty(OWN).setValue(owned);
        koodistoVersioItem.getItemProperty(PLANNED).setValue(planned);
        koodistoVersioItem.getItemProperty(PASSIVE).setValue(passive);
    }

    private boolean checkIfPlanned(Date alkpvm) {

        if (alkpvm != null) {
            return alkpvm.after(new Date());
        }

        return false;
    }

    private boolean checkIfPassive(TilaType tila) {
        if (TilaType.PASSIIVINEN == tila) {
            return true;
        }
        return false;
    }

    private boolean checkIfOwn(KoodistoListType koodisto) {
        return KoodistoApplication.getInstance().hasOrganization(koodisto.getOrganisaatioOid());
    }

    protected String createNameForKoodistoVersion(KoodistoListType koodisto, KoodistoVersioListType koodistoVersio, KieliType locale) {
        KoodistoMetadataType koodistoMetadata = KoodistoHelper.getKoodistoMetadataForLanguage(koodistoVersio, locale);
        String caption = "N/A v. " + koodistoVersio.getVersio();
        if (koodistoMetadata != null && StringUtils.isNotBlank(koodistoMetadata.getNimi())) {
            caption = koodistoMetadata.getNimi() + " v. " + koodistoVersio.getVersio();
        } else {
            koodistoMetadata = KoodistoHelper.getKoodistoMetadataWithAvailableName(koodisto.getLatestKoodistoVersio());
            if (koodistoMetadata != null) {
                caption = koodistoMetadata.getNimi() + " v. " + koodistoVersio.getVersio();
            }
        }
        return caption;
    }

    private String createNameForKoodistoRyhma(KoodistoRyhmaListType koodistoJoukko, KieliType locale) {
        KoodistoRyhmaMetadataType koodistoJoukkoMetadata = KoodistoHelper.getKoodistoRyhmaMetadataForLanguage(koodistoJoukko, locale);
        String caption = "N/A";
        if (koodistoJoukkoMetadata != null && StringUtils.isNotBlank(koodistoJoukkoMetadata.getNimi())) {
            caption = koodistoJoukkoMetadata.getNimi();
        } else if (koodistoJoukko.getKoodistoRyhmaUri() != null) {
            caption = "(" + koodistoJoukko.getKoodistoRyhmaUri() + ")";
        }
        return caption;
    }

    protected String createNameForKoodisto(KoodistoListType koodisto, KieliType locale) {
        String subCaption = "N/A";
        KoodistoMetadataType koodistoMetadata = KoodistoHelper.getKoodistoMetadataForLanguage(koodisto.getLatestKoodistoVersio(), locale);
        if (koodistoMetadata != null && StringUtils.isNotBlank(koodistoMetadata.getNimi())) {
            subCaption = koodistoMetadata.getNimi();
        } else if (koodisto.getKoodistoUri() != null) {
            koodistoMetadata = KoodistoHelper.getKoodistoMetadataWithAvailableName(koodisto.getLatestKoodistoVersio());
            if (koodistoMetadata != null) {
                subCaption = koodistoMetadata.getNimi();
            } else {
                subCaption = "(" + koodisto.getKoodistoUri() + ")";
            }
        }
        return subCaption;
    }

    protected abstract void koodistoRyhmaClicked(KoodistoRyhmaListType koodistoRyhmaDTO);

    protected abstract void koodistoVersioClicked(KoodistoListType koodistoSimpleDTO, KoodistoVersioListType koodistoVersioDTO);

    private class KoodistoTreeEventListener implements ValueChangeListener, ExpandListener, CollapseListener {

        private static final int SPACING_HEIGHT = 20;
        private static final int CONFIRM_DIALOG_WIDTH = 380;
        private static final int CONFIRM_DIALOG_HEIGHT = 260;

        public KoodistoTreeEventListener() {
        }

        private void handleValueChange(Object itemId) {

            koodistoTree.removeListener((ExpandListener) this);
            koodistoTree.removeListener((CollapseListener) this);
            if (koodistoTree.isExpanded(itemId)) {
                koodistoTree.collapseItem(itemId);
            } else {
                koodistoTree.expandItem(itemId);
            }

            koodistoTree.addListener((ExpandListener) this);
            koodistoTree.addListener((CollapseListener) this);

            handleSelect(itemId);

        }

        private void handleSelect(Object itemId) {

            if (itemId instanceof KoodistoListType) {
                koodistoTree.removeListener((ValueChangeListener) this);
                koodistoTree.select(selectedItemId);
                koodistoTree.addListener((ValueChangeListener) this);
            } else {
                selectedItemId = itemId;
                if (itemId instanceof KoodistoRyhmaListType) {
                    koodistoRyhmaClicked((KoodistoRyhmaListType) itemId);
                } else if (itemId instanceof KoodistoVersioListType) {
                    Object parent = rootContainer.getParent(itemId);
                    koodistoVersioClicked((KoodistoListType) parent, (KoodistoVersioListType) itemId);
                }
            }
        }

        @Override
        public void nodeExpand(ExpandEvent event) {
            handleSelect(event.getItemId());
        }

        @Override
        public void nodeCollapse(CollapseEvent event) {
            handleSelect(event.getItemId());
        }

        @Override
        public void valueChange(ValueChangeEvent event) {
            final Object itemId = koodistoTree.getValue();

            // if (itemId != null && selectedItemId != itemId) {
            if (itemId != null && !selectedItemId.equals(itemId)) {

                if (itemId instanceof KoodistoVersioListType && KoodistoApplication.getInstance().getPresenter().isDataChanged()) {

                    final Window confirmDialog = new Window(I18N.getMessage("abstractKoodistoTree.saveConfirmDialog.header"));
                    confirmDialog.setHeight(CONFIRM_DIALOG_HEIGHT, Sizeable.UNITS_PIXELS);
                    confirmDialog.setWidth(CONFIRM_DIALOG_WIDTH, Sizeable.UNITS_PIXELS);
                    confirmDialog.setModal(true);

                    Label label = new Label(I18N.getMessage("abstractKoodistoTree.saveConfirmDialog.bodyText"));
                    confirmDialog.addComponent(label);

                    Label spacing1 = new Label("");
                    spacing1.setHeight(SPACING_HEIGHT, Sizeable.UNITS_PIXELS);
                    confirmDialog.addComponent(spacing1);

                    HorizontalLayout buttonLayout = new HorizontalLayout();

                    Button yesButton = new Button(I18N.getMessage("abstractKoodistoTree.saveConfirmDialog.yesButton"), new Button.ClickListener() {
                        public void buttonClick(Button.ClickEvent event) {
                            KoodistoApplication.getInstance().getMainWindow().removeWindow(confirmDialog);
                            KoodistoApplication.getInstance().getPresenter().setDataChanged(false);
                            handleValueChange(itemId);
                        }
                    });
                    buttonLayout.addComponent(yesButton);

                    Label spacing2 = new Label("");
                    spacing2.setWidth(20, Sizeable.UNITS_PIXELS);
                    buttonLayout.addComponent(spacing2);

                    Button noButton = new Button(I18N.getMessage("abstractKoodistoTree.saveConfirmDialog.noButton"), new Button.ClickListener() {
                        public void buttonClick(Button.ClickEvent event) {
                            KoodistoApplication.getInstance().getMainWindow().removeWindow(confirmDialog);
                            koodistoTree.select(selectedItemId);
                        }
                    });
                    buttonLayout.addComponent(noButton);

                    confirmDialog.addComponent(buttonLayout);

                    KoodistoApplication.getInstance().getMainWindow().addWindow(confirmDialog);

                } else {

                    handleValueChange(itemId);

                }
            }
        }
    }

    public TextField getSearchBox() {
        return searchBox;
    }

    public CheckBox getShowOwn() {
        return showOwn;
    }

    public CheckBox getShowPlanned() {
        return showPlanned;
    }

    public CheckBox getShowPassive() {
        return showPassive;
    }

    public void selectKoodistoTreeItem(String uri, int version) {
        Collection<?> visibleItems = rootContainer.getItemIds();
        for (Object itemId : visibleItems) {
            if (itemId instanceof KoodistoVersioAndKoodistoRyhma
                    && (((KoodistoVersioAndKoodistoRyhma) itemId).getKoodistoUri().equals(uri) && ((KoodistoVersioAndKoodistoRyhma) itemId).getVersio() == version)) {
                Object parent = rootContainer.getParent(itemId);
                if (parent != null) {
                    if (rootContainer.getParent(parent) != null) {
                        koodistoTree.expandItem(rootContainer.getParent(parent));
                    }
                    koodistoTree.expandItem(parent);
                }
                koodistoTree.expandItem(itemId);
                koodistoTree.select(itemId);
                break;
            }
        }
    }

    public void setTextfilter(SimpleStringFilter textfilter) {
        this.textfilter = textfilter;
    }

}
