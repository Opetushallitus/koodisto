package fi.vm.sade.koodisto.ui;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.Window.Notification;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.common.configuration.KoodistoConfiguration;
import fi.vm.sade.koodisto.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.types.common.*;
import fi.vm.sade.koodisto.ui.koodi.*;
import fi.vm.sade.koodisto.ui.koodi.subkoodi.KoodiRelations;
import fi.vm.sade.koodisto.ui.koodi.subkoodi.KoodiRelations.KoodiRelation;
import fi.vm.sade.koodisto.ui.koodi.subkoodi.SubKoodiEdit;
import fi.vm.sade.koodisto.ui.koodisto.*;
import fi.vm.sade.koodisto.ui.service.KoodiUiService;
import fi.vm.sade.koodisto.ui.service.KoodistoPermissionService;
import fi.vm.sade.koodisto.ui.service.KoodistoUiService;
import fi.vm.sade.koodisto.ui.tree.KoodistoTreeColumn;
import fi.vm.sade.koodisto.ui.util.KoodistoGenericFaultErrorCodeResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Scope("prototype")
public class KoodistoPresenter implements Serializable {

    private static final int SPACING_WIDTH = 20;
    private static final String FULL = "100%";
    private static final int SPACING_HEIGHT = 20;
    private static final int CONFIRM_DIALOG_HEIGHT = 260;
    private static final int CONFIRM_DIALOG_WIDTH = 380;

    private static final Logger log = LoggerFactory.getLogger(KoodistoPresenter.class);

    private Window mainWindow;

    @Autowired
    private KoodistoUiService koodistoUiService;

    @Autowired
    private KoodiUiService koodiUiService;

    @Autowired
    private KoodistoConfiguration koodistoConfiguration;

    @Autowired
    private KoodistoPermissionService koodistoPermissionService;

    private Window modalDialog;

    private KoodistoCreate koodistoCreateView;
    private KoodiCreate koodiCreateView;

    private KoodistoTreeColumn koodistoTreeColumn;
    private VerticalLayout koodistoColumn;
    private VerticalLayout koodiColumn;

    private TabSheet koodistoTabSheet;
    private TabSheet koodiTabSheet;

    private KoodistoStateDialog koodistoStateView;
    private KoodiStateDialog koodiStateView;

    private KoodistoDeleteDialog koodistoDeleteView;
    private KoodiDeleteDialog koodiDeleteView;

    private KoodistoDetails koodistoDetails;
    private KoodistoEdit koodistoEdit;

    private VerticalLayout koodiDetailsTab;
    private KoodiDetails koodiDetails;

    private KoodiEdit koodiEdit;

    private SubKoodis subKoodisSisaltyy;
    private SubKoodis subKoodisSisaltyvat;
    private SubKoodis subKoodisRinnasteiset;
    private SubKoodiEdit subKoodiEdit;
    private SubKoodiDeleteDialog subKoodiDeleteView;

    public enum KoodiTabSheetSelection {
        KOODI_DETAILS, KOODI_EDIT
    }

    public enum KoodistoTabSheetSelection {
        KOODISTO_DETAILS, KOODISTO_EDIT
    }

    public void initialize(KoodistoApplication koodistoApplication) {
        final HorizontalLayout layout = new HorizontalLayout();

        mainWindow = new Window(I18N.getMessage("koodisto.title"), layout);
        koodistoApplication.setMainWindow(mainWindow);

        createKoodistoTreeColumn();
        layout.addComponent(koodistoTreeColumn);

        createKoodistoColumn();
        layout.addComponent(koodistoColumn);

        createKoodiColumn();
        layout.addComponent(koodiColumn);

        layout.setWidth(FULL);
        layout.setExpandRatio(koodistoTreeColumn, 1f);
        layout.setExpandRatio(koodistoColumn, 2f);
        layout.setExpandRatio(koodiColumn, 2f);

        koodistoCreateView = new KoodistoCreate(this);
        koodistoDeleteView = new KoodistoDeleteDialog(this);
        koodiCreateView = new KoodiCreate(this);

        koodiDeleteView = new KoodiDeleteDialog(this);
        subKoodiEdit = new SubKoodiEdit(this);
        subKoodiDeleteView = new SubKoodiDeleteDialog(this);

        koodistoStateView = new KoodistoStateDialog(this);
        koodiStateView = new KoodiStateDialog(this);

        modalDialog = new Window();
    }

    public void addKoodisto(KoodistoRyhmaListType selectedKoodistoRyhma, KoodistoType newKoodisto) {
        try {
            KoodistoType createdKoodisto = koodistoUiService.create(newKoodisto, selectedKoodistoRyhma);

            mainWindow.removeWindow(modalDialog);

            showKoodistoAndSelectItInKoodistoTree(createdKoodisto);

            koodistoDetails.addConfirmationMessage(I18N.getMessage("koodistoCreate.koodistoCreated"));
        } catch (GenericFault e) {
            koodistoCreateView.getKoodistoForm().addErrorMessage(KoodistoGenericFaultErrorCodeResolver.getErrorMessageForGenericFault(e));
        }
    }

    private void showKoodistoAndSelectItInKoodistoTree(KoodistoType koodisto) {
        koodistoDetails.showKoodisto(koodisto);
        koodistoEdit.showKoodisto(koodisto);
        koodistoTabSheet.setSelectedTab(koodistoDetails);
        koodistoColumn.setVisible(true);
        koodiColumn.setVisible(false);
        koodistoTreeColumn.refreshKoodistoTree();
        koodistoTreeColumn.selectKoodistoTreeItem(koodisto.getKoodistoUri(), koodisto.getVersio());
    }

    public void addRelation(KoodiUriAndVersioType ylakoodi, KoodiUriAndVersioType alakoodi, SuhteenTyyppiType suhteenTyyppi) {
        koodiUiService.addRelation(ylakoodi, alakoodi, suhteenTyyppi);
    }

    public void addRelation(KoodiUriAndVersioType ylakoodi, Set<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi) {
        koodiUiService.addRelation(ylakoodi, alakoodis, suhteenTyyppi);
    }

    public void cancelKoodiCreation() {
        mainWindow.removeWindow(modalDialog);
        koodistoDetails.addInfoMessage(I18N.getMessage("koodiCreate.koodiCreateCancelled"));
    }

    public void cancelKoodiDelete() {
        mainWindow.removeWindow(modalDialog);
    }

    public void cancelKoodiStateChange(KoodiType koodi) {
        mainWindow.removeWindow(modalDialog);
        KoodiType oldKoodi = koodiUiService.getKoodiByUriAndVersio(koodi.getKoodiUri(), koodi.getVersio());
        koodiEdit.getKoodiForm().refresh(oldKoodi, false);
    }

    public void cancelKoodistoCreation() {
        mainWindow.removeWindow(modalDialog);
        mainWindow.showNotification(I18N.getMessage("koodistoCreate.koodistoCreateCancelled"), Notification.TYPE_HUMANIZED_MESSAGE);
    }

    public void cancelKoodistoDelete() {
        mainWindow.removeWindow(modalDialog);
    }

    public void cancelKoodistoStateChange(KoodistoType koodisto) {
        mainWindow.removeWindow(modalDialog);
        KoodistoType oldKoodisto = koodistoUiService.getKoodistoByUriAndVersion(koodisto.getKoodistoUri(), koodisto.getVersio());
        koodistoEdit.getKoodistoForm().refresh(oldKoodisto, false);
    }

    public void cancelRelationRemove() {
        mainWindow.removeWindow(modalDialog);
    }

    public void addKoodi(KoodistoType koodisto, KoodiType newKoodi) {
        try {
            KoodiType createdKoodi = koodiUiService.create(koodisto, newKoodi);
            mainWindow.removeWindow(modalDialog);
            koodistoTreeColumn.refreshKoodistoTree();
            koodistoTreeColumn.selectKoodistoTreeItem(koodisto.getKoodistoUri(), koodisto.getVersio() + 1);

            koodistoDetails.refresh();
            koodistoDetails.addConfirmationMessage(addDateAndName(I18N.getMessage("koodiCreate.koodiCreated")));

            showKoodi(createdKoodi, KoodiTabSheetSelection.KOODI_DETAILS);
        } catch (GenericFault e) {
            koodiCreateView.getKoodiForm().addErrorMessage(KoodistoGenericFaultErrorCodeResolver.getErrorMessageForCode(e.getFaultInfo().getErrorCode()));
        }

    }

    private void createKoodiColumn() {
        koodiColumn = new VerticalLayout();
        koodiColumn.setWidth(FULL);
        koodiColumn.setHeight(FULL);
        koodiColumn.setMargin(false, true, false, false);

        koodiTabSheet = new TabSheet();
        koodiColumn.addComponent(koodiTabSheet);

        koodiDetails = new KoodiDetails(this);
        subKoodisSisaltyy = new SubKoodis(this, true, SuhteenTyyppiType.SISALTYY);
        subKoodisSisaltyvat = new SubKoodis(this, false, SuhteenTyyppiType.SISALTYY);
        subKoodisRinnasteiset = new SubKoodis(this, false, SuhteenTyyppiType.RINNASTEINEN);

        koodiDetailsTab = new VerticalLayout();
        koodiDetailsTab.setMargin(true);
        koodiDetailsTab.addComponent(koodiDetails);
        koodiDetailsTab.addComponent(subKoodisSisaltyy);
        koodiDetailsTab.addComponent(subKoodisSisaltyvat);
        koodiDetailsTab.addComponent(subKoodisRinnasteiset);

        koodiTabSheet.addTab(koodiDetailsTab, I18N.getMessage("koodiDetails.title"));

        koodiEdit = new KoodiEdit(this);
        koodiTabSheet.addTab(koodiEdit, I18N.getMessage("koodiEdit.title"));
        koodiColumn.setVisible(false);
    }

    private void createKoodistoColumn() {
        koodistoColumn = new VerticalLayout();
        koodistoColumn.setWidth(FULL);
        koodistoColumn.setHeight(FULL);
        koodistoColumn.setMargin(false, true, false, false);

        koodistoTabSheet = new TabSheet();
        koodistoColumn.addComponent(koodistoTabSheet);

        koodistoDetails = new KoodistoDetails(this);
        koodistoTabSheet.addTab(koodistoDetails, I18N.getMessage("koodistoDetails.title"));
        koodistoTabSheet.setSelectedTab(koodistoDetails);

        koodistoEdit = new KoodistoEdit(this);
        koodistoTabSheet.addTab(koodistoEdit, I18N.getMessage("koodistoEdit.title"));

        koodistoColumn.setVisible(false);
    }

    private void createKoodistoTreeColumn() {
        koodistoTreeColumn = new KoodistoTreeColumn(this);
        koodistoTreeColumn.setWidth(FULL);
        koodistoTreeColumn.setHeight(FULL);
    }

    public void deleteKoodi(KoodiType koodi) {
        try {
            koodiUiService.delete(koodi);
            KoodistoType koodistoType = koodistoUiService.getKoodistoByUriAndVersion(koodistoDetails.getKoodisto().getKoodistoUri(), koodistoDetails
                    .getKoodisto().getVersio());
            koodistoDetails.showKoodisto(koodistoType);
            koodistoEdit.showKoodisto(koodistoType);
            koodiColumn.setVisible(false);
            koodistoTreeColumn.refreshKoodistoTree();
            koodistoDetails.addConfirmationMessage(I18N.getMessage("koodiDetails.window.deleteKoodi.success"));
        } catch (GenericFault e) {
            if (e.getFaultInfo().getErrorCode().equals("fi.vm.sade.koodisto.service.business.exception.KoodiVersioHasRelationsException")) {
                koodiDetails.addErrorMessage(I18N.getMessage("koodiDetails.window.deleteKoodi.failed.koodiHasRelations"));
            } else {
                koodiDetails.addErrorMessage(I18N.getMessage("koodiDetails.window.deleteKoodi.failed"));
            }
        }

        mainWindow.removeWindow(modalDialog);
    }

    public void deleteKoodisto(KoodistoType koodisto) {
        try {
            koodistoUiService.delete(koodisto);
            removeLastDayActiveFromLatestKoodisto(koodisto);
            koodistoColumn.setVisible(false);
            koodiColumn.setVisible(false);
            koodistoTreeColumn.refreshKoodistoTree();
            KoodistoApplication.getInstance().getMainWindow().showNotification(I18N.getMessage("koodistoDetails.deleteKoodisto.success"));
        } catch (GenericFault e) {
            if (e.getFaultInfo().getErrorCode().equals("fi.vm.sade.koodisto.service.business.exception.KoodiVersioHasRelationsException")) {
                koodistoDetails.addErrorMessage(I18N.getMessage("koodistoDetails.deleteKoodisto.failed.koodiHasRelations"));
            } else if (e.getFaultInfo().getErrorCode().equals("fi.vm.sade.koodisto.service.business.exception.KoodiVersioNotPassiivinenException")) {
                koodistoDetails.addErrorMessage(I18N.getMessage("koodistoDetails.deleteKoodisto.failed.koodiNotPassive"));
            } else {
                koodistoDetails.addErrorMessage(I18N.getMessage("koodistoDetails.deleteKoodisto.failed"));
            }
        }
        mainWindow.removeWindow(modalDialog);
    }

    private void removeLastDayActiveFromLatestKoodisto(KoodistoType koodisto) {
         KoodistoType latestKoodisto = koodistoUiService.getKoodistoByUriIfAvailable(koodisto.getKoodistoUri());
        if (latestKoodisto != null) {
            latestKoodisto.setVoimassaLoppuPvm(null);
            koodistoUiService.update(latestKoodisto);
        }
    }

    public void koodiStateChange(KoodiType koodi) {
        mainWindow.removeWindow(modalDialog);
        confirmedUpdateKoodi(koodi);
    }

    public void koodistoStateChange(KoodistoType koodisto) {
        mainWindow.removeWindow(modalDialog);
        confirmedUpdateKoodisto(koodisto);
    }

    public KoodiType getKoodiByUriAndVersio(String koodiUri, int versio) {
        return koodiUiService.getKoodiByUriAndVersio(koodiUri, versio);
    }

    public String getKoodiResourceUri(String koodistoUri, String koodiUri) {
        return koodistoConfiguration.getKoodiResourceUri(koodistoUri, koodiUri);
    }

    public String getKoodistoResourceUri(String koodistoUri) {
        return koodistoConfiguration.getKoodistoResourceUri(koodistoUri);
    }

    public Map<String, KoodistoType> getKoodistosByUrisMap(Set<String> koodistoUris) {
        return koodistoUiService.getKoodistosByUrisMap(koodistoUris);
    }

    public List<KoodiType> listAllKoodiVersiosByUri(String koodiUri) {
        return koodiUiService.listAllKoodiVersiosByUri(koodiUri);
    }

    public List<KoodiType> listKoodiByRelation(String koodiUri, int versio, boolean isChild, SuhteenTyyppiType suhteenTyyppi) {
        return koodiUiService.listKoodiByRelation(koodiUri, versio, isChild, suhteenTyyppi);
    }

    public List<KoodiType> listKoodisByKoodisto(String koodistoUri, Integer koodistoVersio) {
        return koodiUiService.listKoodisByKoodisto(koodistoUri, koodistoVersio);
    }

    public List<KoodistoRyhmaListType> listKoodistoRyhmas() {
        return koodistoUiService.listKoodistoRyhmas();
    }

    public void removeRelation(SubKoodis source, KoodiUriAndVersioType ylakoodi, KoodiUriAndVersioType alakoodi, SuhteenTyyppiType suhteenTyyppi) {
        koodiUiService.removeRelation(ylakoodi, alakoodi, suhteenTyyppi);
        if (source.equals(subKoodisRinnasteiset)) {
            subKoodisRinnasteiset.refresh();
        } else if (source.equals(subKoodisSisaltyvat)) {
            subKoodisSisaltyvat.refresh();
        } else if (source.equals(subKoodisSisaltyy)) {
            subKoodisSisaltyy.refresh();
        }
        mainWindow.removeWindow(modalDialog);

        koodistoTreeColumn.refreshKoodistoTree();
    }

    public void removeRelation(KoodiUriAndVersioType ylakoodi, KoodiUriAndVersioType alakoodi, SuhteenTyyppiType suhteenTyyppi) {
        koodiUiService.removeRelation(ylakoodi, alakoodi, suhteenTyyppi);
    }

    public void removeRelation(KoodiUriAndVersioType ylakoodi, Set<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi) {
        koodiUiService.removeRelation(ylakoodi, alakoodis, suhteenTyyppi);
    }

    private void showModalDialog(VerticalLayout view) {
        modalDialog.setModal(true);
        modalDialog.center();
        modalDialog.getContent().setSizeUndefined();
        modalDialog.addComponent(view);
        modalDialog.setSizeUndefined();
        mainWindow.addWindow(modalDialog);
    }

    public void showCreateKoodistoView(KoodistoRyhmaListType selectedKoodistoRyhma) {
        koodistoCreateView.refresh(selectedKoodistoRyhma);
        modalDialog.removeAllComponents();
        modalDialog.setCaption(I18N.getMessage("koodistoCreateDialog.title"));
        showModalDialog(koodistoCreateView);
    }

    public void showCreateKoodiView(KoodistoType koodisto) {
        koodiCreateView.refresh(koodisto);
        modalDialog.removeAllComponents();
        modalDialog.setCaption(I18N.getMessage("koodistoDetails.createKoodi.window.title"));
        showModalDialog(koodiCreateView);
    }

    public void showDeleteKoodistoView(KoodistoType koodisto) {
        koodistoDeleteView.refresh(koodisto);
        modalDialog.removeAllComponents();
        modalDialog.setCaption(I18N.getMessage("koodistoDetails.deleteKoodisto.window.title"));
        showModalDialog(koodistoDeleteView);
    }

    public void showStateKoodistoView(KoodistoType koodisto) {
        koodistoStateView.refresh(koodisto);
        modalDialog.removeAllComponents();
        modalDialog.setCaption(I18N.getMessage("koodistoDetails.stateKoodisto.window.title"));
        showModalDialog(koodistoStateView);
    }

    public void showStateKoodiView(KoodiType koodi) {
        koodiStateView.refresh(koodi);
        modalDialog.removeAllComponents();
        modalDialog.setCaption(I18N.getMessage("koodiDetails.window.stateKoodi.title"));
        showModalDialog(koodiStateView);
    }

    public void showDeleteKoodiView(KoodiType koodi) {
        koodiDeleteView.refresh(koodi);
        modalDialog.removeAllComponents();
        modalDialog.setCaption(I18N.getMessage("koodiDetails.window.deleteKoodi.title"));
        showModalDialog(koodiDeleteView);
    }

    public void showRemoveRelationView(boolean isChild, KoodiType koodi, KoodiType relatedKoodi, SubKoodis source, SuhteenTyyppiType suhteenTyyppi) {
        subKoodiDeleteView.refresh(isChild, koodi, relatedKoodi, source, suhteenTyyppi);
        modalDialog.removeAllComponents();
        modalDialog.setCaption(I18N.getMessage("subKoodis.window.deleteRelation.title"));
        showModalDialog(subKoodiDeleteView);
    }

    private void showKoodi(KoodiType koodi, KoodiTabSheetSelection tabSheetSelection) {
        koodiDetails.showKoodi(koodi);
        koodiEdit.showKoodi(koodi);

        switch (tabSheetSelection) {
        case KOODI_DETAILS:
            koodiTabSheet.setSelectedTab(koodiDetailsTab);
            break;
        case KOODI_EDIT:
            koodiTabSheet.setSelectedTab(koodiEdit);
            break;
        default:
            break;
        }
        koodiColumn.setVisible(true);
        subKoodisRinnasteiset.refresh(koodi);
        subKoodisSisaltyvat.refresh(koodi);
        subKoodisSisaltyy.refresh(koodi);
    }

    public void showKoodi(String koodiUri, int koodiVersio) {
        KoodiType koodi = koodiUiService.getKoodiByUriAndVersio(koodiUri, koodiVersio);
        showKoodi(koodi, KoodiTabSheetSelection.KOODI_DETAILS);
    }

    public void showKoodisto(final KoodistoType koodisto, final KoodistoTabSheetSelection tabSheetSelection, boolean showKoodiColumn) {

        if (isDataChanged()) {
            final Window confirmDialog = new Window("Muutoksia ei ole tallennettu!");
            confirmDialog.setHeight(CONFIRM_DIALOG_HEIGHT, Sizeable.UNITS_PIXELS);
            confirmDialog.setWidth(CONFIRM_DIALOG_WIDTH, Sizeable.UNITS_PIXELS);
            confirmDialog.setModal(true);

            Label label = new Label("Et ole tallentanut tekemiäsi muutoksia. Oletko varma että haluat jatkaa?");
            confirmDialog.addComponent(label);

            Label spacing1 = new Label("");
            spacing1.setHeight(SPACING_HEIGHT, Sizeable.UNITS_PIXELS);
            confirmDialog.addComponent(spacing1);

            HorizontalLayout buttonLayout = new HorizontalLayout();

            Button yesButton = new Button("Kyllä", new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {
                    KoodistoApplication.getInstance().getMainWindow().removeWindow(confirmDialog);
                    setDataChanged(false);
                    showKoodisto(koodisto, tabSheetSelection, false);
                }
            });
            buttonLayout.addComponent(yesButton);

            Label spacing2 = new Label("");
            spacing2.setWidth(SPACING_WIDTH, Sizeable.UNITS_PIXELS);
            buttonLayout.addComponent(spacing2);

            Button noButton = new Button("Ei", new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {
                    KoodistoApplication.getInstance().getMainWindow().removeWindow(confirmDialog);
                }
            });
            buttonLayout.addComponent(noButton);

            confirmDialog.addComponent(buttonLayout);

            KoodistoApplication.getInstance().getMainWindow().addWindow(confirmDialog);

            return;
        }

        koodistoDetails.showKoodisto(koodisto);
        koodistoEdit.showKoodisto(koodisto);

        switch (tabSheetSelection) {

        case KOODISTO_DETAILS:
            koodistoTabSheet.setSelectedTab(koodistoDetails);
            break;
        case KOODISTO_EDIT:
            koodistoTabSheet.setSelectedTab(koodistoEdit);
            break;
        default:
            log.warn("Unknown tab selection");
        }

        koodistoColumn.setVisible(true);
        koodiColumn.setVisible(showKoodiColumn);
    }

    public void showKoodisto(String koodistoUri, int versio, boolean showKoodiColumn) {
        showKoodisto(koodistoUiService.getKoodistoByUriAndVersion(koodistoUri, versio), KoodistoTabSheetSelection.KOODISTO_DETAILS, showKoodiColumn);
    }

    public void hideKoodisto() {
        if (koodistoColumn != null) {
            koodistoColumn.setVisible(false);
        }
    }

    public void hideKoodi() {
        if (koodiColumn != null) {
            koodiColumn.setVisible(false);
        }
    }

    public void updateKoodi(KoodiType koodi) {
        if (koodi.getTila() == TilaType.PASSIIVINEN) {
            KoodiType oldKoodi = koodiUiService.getKoodiByUriAndVersio(koodi.getKoodiUri(), koodi.getVersio());
            if (oldKoodi.getTila() != TilaType.PASSIIVINEN) {
                showStateKoodiView(koodi);
            }
        } else {
            confirmedUpdateKoodi(koodi);
        }
    }

    private void confirmedUpdateKoodi(KoodiType koodi) {
        try {
            KoodiType updated = koodiUiService.update(koodi);

            showKoodi(updated, KoodiTabSheetSelection.KOODI_EDIT);
            koodiTabSheet.setSelectedTab(koodistoDetails);

            koodistoTreeColumn.refreshKoodistoTree();

            if (koodistoDetails.getKoodisto().getTila() == TilaType.HYVAKSYTTY) {
                KoodistoType latestKoodistoType = getLatestKoodistoByUri(koodistoDetails.getKoodisto().getKoodistoUri());
                showKoodisto(latestKoodistoType.getKoodistoUri(), latestKoodistoType.getVersio(), false);
                koodistoTreeColumn.selectKoodistoTreeItem(koodistoDetails.getKoodisto().getKoodistoUri(), koodistoDetails.getKoodisto().getVersio());

                koodistoDetails.addConfirmationMessage(addDateAndName(I18N.getMessage("koodiEdit.save.success")));
            } else {
                showKoodisto(koodistoDetails.getKoodisto().getKoodistoUri(), koodistoDetails.getKoodisto().getVersio(), true);
                koodistoTreeColumn.selectKoodistoTreeItem(koodistoDetails.getKoodisto().getKoodistoUri(), koodistoDetails.getKoodisto().getVersio());

                koodiEdit.getKoodiForm().addConfirmationMessage(addDateAndName(I18N.getMessage("koodiEdit.save.success")));
            }

        } catch (GenericFault e) {
            koodiEdit.getKoodiForm().addErrorMessage(KoodistoGenericFaultErrorCodeResolver.getErrorMessageForCode(e.getFaultInfo().getErrorCode()));
        }
    }

    public void updateKoodisto(KoodistoType koodisto) {
        if (koodisto.getTila() == TilaType.PASSIIVINEN) {
            KoodistoType oldKoodisto = koodistoUiService.getKoodistoByUriAndVersion(koodisto.getKoodistoUri(), koodisto.getVersio());
            if (oldKoodisto.getTila() != TilaType.PASSIIVINEN) {
                showStateKoodistoView(koodisto);
            }
        } else {
            confirmedUpdateKoodisto(koodisto);
        }
        KoodiType currentKoodi = koodiEdit.getKoodiForm().getCurrentKoodi();
        if (currentKoodi != null) {
            try {
                KoodiType updatedKoodi = koodiUiService.getKoodiByUriAndVersio(currentKoodi.getKoodiUri(), currentKoodi.getVersio());
                koodiEdit.getKoodiForm().refresh(updatedKoodi, false);
            } catch (KoodiNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void confirmedUpdateKoodisto(KoodistoType koodisto) {
        try {
            KoodistoType updated = koodistoUiService.update(koodisto);
            showKoodistoAndSelectItInKoodistoTree(updated);
            koodistoDetails.addConfirmationMessage(addDateAndName(I18N.getMessage("koodistoEdit.save.success")));

        } catch (GenericFault e) {
            koodistoEdit.getKoodistoForm().addErrorMessage(KoodistoGenericFaultErrorCodeResolver.getErrorMessageForGenericFault(e));
        }
    }

    public boolean userCanAddKoodiToKoodisto(KoodistoType koodisto) {
        return koodistoPermissionService.userCanAddKoodiToKoodisto(koodisto);
    }

    public boolean userCanCreateReadUpdateAndDelete() {
        return koodistoPermissionService.userCanCreateReadUpdateAndDelete();
    }

    public boolean userCanDeleteKoodi(KoodiType koodi) {
        return koodistoPermissionService.userCanDeleteKoodi(koodi);
    }

    public boolean userCanDeleteKoodisto(KoodistoType koodisto) {
        return koodistoPermissionService.userCanDeleteKoodisto(koodisto);
    }

    public boolean userCanEditKoodi(KoodiType koodi) {
        return koodistoPermissionService.userCanEditKoodi(koodi);
    }

    public boolean userCanEditKoodisto(KoodistoType koodisto) {
        return koodistoPermissionService.userCanEditKoodisto(koodisto);
    }

    public boolean userCanRead() {
        return koodistoPermissionService.userCanRead();
    }

    public String getBaseUri() {
        return koodistoConfiguration.getBaseUri();
    }

    public void showSubKoodiEdit(KoodiType koodi, boolean isChild, SuhteenTyyppiType suhteenTyyppi) {
        subKoodiEdit.refresh(koodi, isChild, suhteenTyyppi);
        modalDialog.removeAllComponents();
        String windowLabel = "";
        if (suhteenTyyppi == SuhteenTyyppiType.RINNASTEINEN) {
            windowLabel = I18N.getMessage("subKoodis.subKoodiEdit.parallel.window.title");
        } else if (suhteenTyyppi == SuhteenTyyppiType.SISALTYY && !isChild) {
            windowLabel = I18N.getMessage("subKoodis.subKoodiEdit.parent.window.title");
        } else if (suhteenTyyppi == SuhteenTyyppiType.SISALTYY && isChild) {
            windowLabel = I18N.getMessage("subKoodis.subKoodiEdit.child.window.title");
        }

        modalDialog.setCaption(windowLabel);
        modalDialog.setModal(true);
        modalDialog.center();
        modalDialog.setWidth("70%");
        modalDialog.setHeight(FULL);
        modalDialog.addComponent(subKoodiEdit);

        mainWindow.addWindow(modalDialog);
    }

    public void addAndRemoveRelations(KoodiRelations koodiRelations) {
        try {
            for (Map.Entry<KoodiRelation, Set<KoodiUriAndVersioType>> e : koodiRelations.getToAdd().entrySet()) {
                KoodiUriAndVersioType ylakoodi = e.getKey().getYlakoodi();
                SuhteenTyyppiType suhteenTyyppi = e.getKey().getSuhteenTyyppi();
                Set<KoodiUriAndVersioType> alakoodis = e.getValue();
                koodiUiService.addRelation(ylakoodi, alakoodis, suhteenTyyppi);
            }
        } catch (GenericFault e) {
            mainWindow.showNotification(I18N.getMessage("subKoodiEdit.addRelation.failed"), Notification.TYPE_ERROR_MESSAGE);
        }

        try {
            for (Map.Entry<KoodiRelation, Set<KoodiUriAndVersioType>> e : koodiRelations.getToRemove().entrySet()) {
                KoodiUriAndVersioType ylakoodi = e.getKey().getYlakoodi();
                SuhteenTyyppiType suhteenTyyppi = e.getKey().getSuhteenTyyppi();
                Set<KoodiUriAndVersioType> alakoodis = e.getValue();
                koodiUiService.removeRelation(ylakoodi, alakoodis, suhteenTyyppi);
            }
        } catch (GenericFault e) {
            mainWindow.showNotification(I18N.getMessage("subKoodiEdit.removeRelation.failed"), Notification.TYPE_ERROR_MESSAGE);
        }

        mainWindow.removeWindow(modalDialog);
        koodistoTreeColumn.refreshKoodistoTree();
        subKoodisRinnasteiset.refresh();
        subKoodisSisaltyvat.refresh();
        subKoodisSisaltyy.refresh();

        if (koodistoDetails.getKoodisto().getTila() == TilaType.HYVAKSYTTY) {
            showKoodisto(koodistoDetails.getKoodisto().getKoodistoUri(), koodistoDetails.getKoodisto().getVersio() + 1, false);
            koodistoTreeColumn.selectKoodistoTreeItem(koodistoDetails.getKoodisto().getKoodistoUri(), koodistoDetails.getKoodisto().getVersio());
        }

        koodiDetails.addConfirmationMessage(addDateAndName(I18N.getMessage("subKoodiEdit.saved")));
    }

    private String addDateAndName(String message) {
        String returnString = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        String username = "";

        if (KoodistoApplication.getInstance().getUser().getAuthentication().getPrincipal() instanceof fi.vm.sade.security.SadeUserDetailsWrapper) {
            fi.vm.sade.security.SadeUserDetailsWrapper user = (fi.vm.sade.security.SadeUserDetailsWrapper) KoodistoApplication.getInstance().getUser()
                    .getAuthentication().getPrincipal();
            username = user.getUsername();
        }

        if (KoodistoApplication.getInstance().getUser().getAuthentication().getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) KoodistoApplication.getInstance()
                    .getUser().getAuthentication().getPrincipal();
            username = user.getUsername();
        }

        returnString = message + " " + sdf.format(new Date());

        if (!username.isEmpty()) {
            returnString += ", " + username;
        }
        return returnString;
    }

    public void cancelSubKoodiEdit() {
        koodiDetails.addInfoMessage(I18N.getMessage("subKoodiEdit.cancelled"));
        mainWindow.removeWindow(modalDialog);
    }

    public void showUploadKoodistoView(String koodistoUri) {
        new UploadKoodisto(this, mainWindow, koodistoUri);
    }

    public void upload(Window dialog, InputStream csvData, String koodistoUri, Format format, Encoding encoding) {
        try {
            koodiUiService.upload(csvData, koodistoUri, format, encoding);
            mainWindow.removeWindow(dialog);
            koodistoDetails.addConfirmationMessage(I18N.getMessage("uploadKoodisto.sending.success"));
        } catch (Exception e) {
            log.error("Koodiston lähetys epäonnistui", e);
            koodistoDetails.addErrorMessage(I18N.getMessage("uploadKoodisto.sending.failed"));
        }
    }

    public void showDownloadKoodistoView(String koodistoUri, int koodistoVersion) {
        new DownloadKoodisto(this, mainWindow, koodistoUri, koodistoVersion);
    }

    public InputStream download(Window dialog, String koodistoUri, Integer koodistoVersion, Format format, String encoding) {

        InputStream inputStream = null;
        try {
            inputStream = koodiUiService.download(koodistoUri, koodistoVersion, format, encoding);
        } catch (IOException e) {
            log.error("Koodiston lataus epäonnistui", e);
        }
        return inputStream;
    }

    public KoodistoType getLatestKoodistoByUri(String koodistoUri) {
        return koodistoUiService.getKoodistoByUri(koodistoUri);
    }

    public KoodiType getLatestKoodiByUri(String koodiUri) {
        return koodiUiService.getKoodiByUri(koodiUri);
    }

    public KoodistoType getKoodistoByUriAndVersio(String koodistoUri, int versio) {
        return koodistoUiService.getKoodistoByUriAndVersion(koodistoUri, versio);
    }

    public boolean isKoodiEditable(KoodiType koodi) {
        if (koodi.getVersio() != getLatestKoodiByUri(koodi.getKoodiUri()).getVersio()) {
            return false;
        }

        KoodistoType latestKoodisto = getLatestKoodistoByUri(koodi.getKoodisto().getKoodistoUri());
        return koodi.getKoodisto().getKoodistoVersio().contains(latestKoodisto.getVersio());
    }

    public boolean isKoodistoEditable(KoodistoType koodisto) {
        return koodisto.getVersio() == getLatestKoodistoByUri(koodisto.getKoodistoUri()).getVersio();
    }

    private boolean dataChanged = false;

    public void setDataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

}
