package fi.vm.sade.koodisto.ui.koodisto;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.vm.sade.generic.ui.message.ConfirmationMessage;
import fi.vm.sade.generic.ui.message.InfoMessage;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.koodisto.ui.util.KoodiTypeCaptionSorter;
import fi.vm.sade.koodisto.ui.util.KoodiTypeUtil;
import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.vaadin.Oph;

/**
 * @author tommiha
 */
@SuppressWarnings("serial")
public class KoodistoDetails extends VerticalLayout {
    private static final float CSS_LAYOUT_WIDTH = 420f;
    private static final String FULL = "100%";

    private ErrorMessage errorMessage;
    private InfoMessage infoMessage;
    private ConfirmationMessage confirmationMessage;

    private HierarchicalContainer detailContainer;

    private Tree koodiTree;

    private Label kuvausLabel;

    private KoodistoType koodisto;

    private Button deleteKoodistoButton;

    private boolean showKoodiVersios;

    private Set<KoodiType> expandedKoodis = new HashSet<KoodiType>();

    private Button createKoodiButton;

    private CheckBox showVersiosCheckBox;

    private TextField searchBox;

    private Label uriLabel;

    private Button uploadKoodiCSVButton;

    private Button downloadKoodistoCSVButton;

    private Label nimiLabel;

    private KoodistoPresenter presenter;

    public KoodistoDetails(KoodistoPresenter presenter) {
        this.presenter = presenter;
        buildMainLayout();
        initializeComponents();
    }

    private void buildMainLayout() {
        setWidth(FULL);
        setHeight(FULL);
        setMargin(true);
        setSpacing(true);
        this.errorMessage = new ErrorMessage();
        this.infoMessage = new InfoMessage();
        this.confirmationMessage = new ConfirmationMessage();
    }

    private void initializeComponents() {

        this.showKoodiVersios = false;

        uriLabel = new Label();
        nimiLabel = new Label();
        kuvausLabel = new Label();

        nimiLabel.setStyleName(Oph.LABEL_H2);
        nimiLabel.setWidth(FULL);
        kuvausLabel.setWidth(FULL);

        addComponent(uriLabel);
        uriLabel.setWidth(null);
        setComponentAlignment(uriLabel, Alignment.MIDDLE_RIGHT);

        addComponent(nimiLabel);
        addComponent(kuvausLabel);

        detailContainer = new HierarchicalContainer();
        detailContainer.setItemSorter(new KoodiTypeCaptionSorter());

        createKoodiAddingButtons();
        createKoodiSearchBox();

        createKoodiTree(detailContainer);

        addComponent(errorMessage);
        addComponent(infoMessage);
        addComponent(confirmationMessage);
    }

    private void createKoodiTree(HierarchicalContainer container) {
        this.koodiTree = new Tree("Koodit", container);
        koodiTree.setImmediate(false);
        koodiTree.setWidth(FULL);
        koodiTree.setHeight(FULL);
        koodiTree.setItemCaptionPropertyId("caption");

        koodiTree.addListener(new ItemClickListener() {

            public void itemClick(final ItemClickEvent event) {
                KoodiType koodi = (KoodiType) event.getItemId();
                presenter.showKoodi(koodi.getKoodiUri(), koodi.getVersio());
            }
        });

        koodiTree.addListener(new ExpandListener() {

            @Override
            public void nodeExpand(ExpandEvent event) {
                KoodiType parent = (KoodiType) event.getItemId();
                expandedKoodis.add(parent);

                addKoodiVersiosForParentKoodi(parent);
                koodiTree.requestRepaint();
            }
        });

        koodiTree.addListener(new Tree.CollapseListener() {

            @Override
            public void nodeCollapse(CollapseEvent event) {
                KoodiType parent = (KoodiType) event.getItemId();
                expandedKoodis.remove(parent);
            }
        });

        showVersiosCheckBox = new CheckBox(I18N.getMessage("koodistoDetails.showVersios"));
        showVersiosCheckBox.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                KoodistoDetails.this.showKoodiVersios = (Boolean) showVersiosCheckBox.getValue();

                @SuppressWarnings("unchecked")
                Collection<KoodiType> itemIds = (Collection<KoodiType>) detailContainer.getItemIds();
                for (KoodiType id : itemIds) {
                    if (id.getVersio() > 1 && detailContainer.isRoot(id)) {
                        detailContainer.setChildrenAllowed(id, KoodistoDetails.this.showKoodiVersios);
                    }
                }
                koodiTree.requestRepaint();
            }
        });
        showVersiosCheckBox.setImmediate(true);

        addComponent(showVersiosCheckBox);
        addComponent(koodiTree);
    }

    private void addKoodiVersiosForParentKoodi(KoodiType parent) {
        List<KoodiType> versios = presenter.listAllKoodiVersiosByUri(parent.getKoodiUri());
        Collections.sort(versios, new Comparator<KoodiType>() {

            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                return o2.getVersio() - o1.getVersio();
            }
        });

        if (!detailContainer.hasChildren(parent)) {
            for (KoodiType versio : versios) {
                if (versio.getVersio() < parent.getVersio()) {
                    Item versioItem = detailContainer.addItem(versio);
                    if (versioItem != null) {
                        String koodiCaption = KoodiTypeUtil.extractNameForKoodi(versio);
                        versioItem.getItemProperty("caption").setValue(koodiCaption);
                        detailContainer.setParent(versio, parent);
                        detailContainer.setChildrenAllowed(versio, false);
                    }
                }
            }
        }
    }

    private void createKoodiSearchBox() {
        Layout layout = new HorizontalLayout();
        searchBox = new TextField(I18N.getMessage("koodistoDetails.searchKoodis"));
        searchBox.setStyleName(Oph.TEXTFIELD_SEARCH);
        searchBox.setImmediate(true);

        searchBox.addListener(new TextChangeListener() {
            public void textChange(TextChangeEvent event) {
                detailContainer.removeAllContainerFilters();
                detailContainer.addContainerFilter("caption", event.getText(), true, false);
            }
        });
        layout.addComponent(searchBox);
        addComponent(layout);
    }

    private void createKoodiAddingButtons() {
        CssLayout layout = new CssLayout();
        layout.setWidth(CSS_LAYOUT_WIDTH, UNITS_PIXELS);
        layout.setMargin(true, false, true, false);
        createKoodiButton = new Button(I18N.getMessage("koodistoDetails.button.addKoodi"));
        createKoodiButton.addListener(new CreateKoodiButtonEventListener());
        createKoodiButton.addStyleName(Oph.BUTTON_PRIMARY);
        createKoodiButton.addStyleName(Oph.BUTTON_PLUS);
        createKoodiButton.addStyleName(Oph.BUTTON_SMALL);

        deleteKoodistoButton = new Button(I18N.getMessage("koodistoDetails.button.deleteKoodisto"));
        deleteKoodistoButton.addStyleName(Oph.BUTTON_SMALL);
        deleteKoodistoButton.addListener(new DeleteKoodistoButtonEventListener());

        uploadKoodiCSVButton = new Button(I18N.getMessage("koodistoDetails.button.upload"));
        uploadKoodiCSVButton.addStyleName(Oph.BUTTON_PRIMARY);
        uploadKoodiCSVButton.addStyleName(Oph.BUTTON_SMALL);
        uploadKoodiCSVButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.showUploadKoodistoView(koodisto.getKoodistoUri());

            }
        });

        downloadKoodistoCSVButton = new Button(I18N.getMessage("koodistoDetails.button.download"));
        downloadKoodistoCSVButton.addStyleName(Oph.BUTTON_PRIMARY);
        downloadKoodistoCSVButton.addStyleName(Oph.BUTTON_SMALL);
        downloadKoodistoCSVButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.showDownloadKoodistoView(koodisto.getKoodistoUri(), koodisto.getVersio());
            }
        });

        layout.addComponent(createKoodiButton);
        layout.addComponent(deleteKoodistoButton);
        layout.addComponent(uploadKoodiCSVButton);
        layout.addComponent(downloadKoodistoCSVButton);

        addComponent(layout);
    }

    private class CreateKoodiButtonEventListener implements ClickListener {

        public void buttonClick(ClickEvent event) {
            presenter.showCreateKoodiView(koodisto);
        }
    }

    private class DeleteKoodistoButtonEventListener implements ClickListener {

        public void buttonClick(final ClickEvent event) {
            presenter.showDeleteKoodistoView(koodisto);
        }

    }

    public void refresh() {
        showKoodisto(koodisto);
    }

    public void showKoodisto(KoodistoType koodisto) {
        this.koodisto = koodisto;
        String name = "N/A";
        String kuvaus = "";

        KoodistoMetadataType koodistoMetadataDTO = KoodistoHelper.getKoodistoMetadataForLanguage(koodisto, KoodistoHelper.getKieliForLocale(I18N.getLocale()));

        boolean isEditable = presenter.isKoodistoEditable(koodisto);

        resetMessages();

        deleteKoodistoButton.setVisible(presenter.userCanDeleteKoodisto(koodisto));
        createKoodiButton.setVisible(presenter.userCanAddKoodiToKoodisto(koodisto) && isEditable);
        uploadKoodiCSVButton.setVisible(presenter.userCanAddKoodiToKoodisto(koodisto) && isEditable);
        downloadKoodistoCSVButton.setVisible(presenter.userCanRead());

        if (koodistoMetadataDTO == null) {
            koodistoMetadataDTO = KoodistoHelper.getKoodistoMetadataForAnyLanguage(koodisto);
        }

        if (koodistoMetadataDTO != null) {
            if (StringUtils.isNotBlank(koodistoMetadataDTO.getNimi())) {
                name = koodistoMetadataDTO.getNimi();
            }
            if (StringUtils.isNotBlank(koodistoMetadataDTO.getKuvaus())) {
                kuvaus = koodistoMetadataDTO.getKuvaus();
            }
        }

        uriLabel.setValue(presenter.getKoodistoResourceUri(koodisto.getKoodistoUri()));
        nimiLabel.setValue(name);
        kuvausLabel.setValue(kuvaus);

        deleteKoodistoButton.setEnabled(TilaType.PASSIIVINEN.equals(koodisto.getTila()));

        List<KoodiType> koodiList = presenter.listKoodisByKoodisto(koodisto.getKoodistoUri(), koodisto.getVersio());

        detailContainer.removeAllItems();
        detailContainer.addContainerProperty("caption", String.class, "");

        for (KoodiType koodi : koodiList) {
            Item koodiItem = detailContainer.addItem(koodi);
            if (koodiItem == null) {
                continue;
            }
            String koodiCaption = KoodiTypeUtil.extractNameForKoodi(koodi);
            koodiItem.getItemProperty("caption").setValue(koodiCaption);
            boolean childrenAllowed = this.showKoodiVersios && koodi.getVersio() > 1;
            detailContainer.setChildrenAllowed(koodi, childrenAllowed);

            if (expandedKoodis.contains(koodi) && childrenAllowed) {
                addKoodiVersiosForParentKoodi(koodi);
            } else {
                koodiTree.collapseItem(koodi);
            }
        }

        detailContainer.sort(null, null);

    }

    private void resetMessages() {
        errorMessage.resetErrors();
        infoMessage.resetMessages();
        confirmationMessage.resetMessages();
    }

    public Button getDeleteKoodistoButton() {
        return deleteKoodistoButton;
    }

    public Button getCreateKoodiButton() {
        return createKoodiButton;
    }

    public CheckBox getShowVersiosCheckBox() {
        return showVersiosCheckBox;
    }

    public TextField getSearchBox() {
        return searchBox;
    }

    public KoodistoType getKoodisto() {
        return koodisto;
    }

    public void addErrorMessage(String errorMessage) {
        resetMessages();
        this.errorMessage.addError(errorMessage);
    }

    public void addInfoMessage(String infoMessage) {
        resetMessages();
        this.infoMessage.addMessage(infoMessage);
    }

    public void addConfirmationMessage(String confirmationMessage) {
        resetMessages();
        this.confirmationMessage.addMessage(confirmationMessage);
    }
}
