/**
 * 
 */
package fi.vm.sade.koodisto.ui.koodi;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.*;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author kkammone
 * 
 */
@SuppressWarnings("serial")
public class SubKoodis extends VerticalLayout implements ClickListener {
    private final boolean isChild;
    private final SuhteenTyyppiType suhteenTyyppi;

    private Table table;

    private VerticalLayout toggleLayout;

    private Button editButton;

    private KoodistoPresenter presenter;
    private KoodiType koodi;

    public SubKoodis(KoodistoPresenter presenter, boolean isChild, SuhteenTyyppiType suhteenTyyppi) {
        this.presenter = presenter;
        setWidth("100%");
        this.isChild = isChild;
        this.suhteenTyyppi = suhteenTyyppi;

        String labelstring = "";
        if (this.suhteenTyyppi == SuhteenTyyppiType.RINNASTEINEN) {
            labelstring = I18N.getMessage("subKoodis.label.parallel");
        } else if (this.suhteenTyyppi == SuhteenTyyppiType.SISALTYY && !isChild) {
            labelstring = I18N.getMessage("subKoodis.label.parent");
        } else if (this.suhteenTyyppi == SuhteenTyyppiType.SISALTYY && isChild) {
            labelstring = I18N.getMessage("subKoodis.label.child");
        }

        Button label = new Button(labelstring);
        label.setStyleName(BaseTheme.BUTTON_LINK);
        addComponent(label);
        label.addListener(this);

        toggleLayout = new VerticalLayout();
        toggleLayout.setWidth("100%");
        toggleLayout.setVisible(false);

        addComponent(toggleLayout);

        table = new Table("");

        table.addContainerProperty("koodistonimi", String.class, "", I18N.getMessage("subKoodis.header.koodistoNimi"), null, null);
        table.addContainerProperty("koodi", String.class, "", I18N.getMessage("subKoodis.header.koodi"), null, null);
        table.addContainerProperty("kuvaus", String.class, "", I18N.getMessage("subKoodis.header.kuvaus"), null, null);

        table.addContainerProperty("deleteRelation", Button.class, "", null, null, null);

        table.setWidth("100%");
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
        table.setCellStyleGenerator(null);

        editButton = new Button(I18N.getMessage("subKoodis.button.edit"));

        toggleLayout.addComponent(editButton);
        toggleLayout.setComponentAlignment(editButton, Alignment.BOTTOM_RIGHT);

        editButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                SubKoodis.this.presenter.showSubKoodiEdit(koodi, SubKoodis.this.isChild, SubKoodis.this.suhteenTyyppi);
            }
        });
        toggleLayout.addComponent(table);
    }

    private void addKoodiToTable(final KoodiType koodi, final KoodiType relatedKoodi, Map<String, KoodistoType> koodistoMap, boolean isEditable) {
        KoodiMetadataType koodiMetadata = KoodistoHelper.getKoodiMetadataForLanguage(relatedKoodi, KoodistoHelper.getKieliForLocale(I18N.getLocale()));

        String nimi = "N/A";
        String kuvaus = "N/A";
        String koodistoNimi = "N/A";

        KoodistoMetadataType koodistoMetadata = KoodistoHelper.getKoodistoMetadataForLanguage(koodistoMap.get(relatedKoodi.getKoodisto().getKoodistoUri()),
                KoodistoHelper.getKieliForLocale(I18N.getLocale()));

        if (koodiMetadata != null) {
            if (StringUtils.isNotBlank(koodiMetadata.getNimi())) {
                nimi = koodiMetadata.getNimi();
            }
            if (StringUtils.isNotBlank(koodiMetadata.getKuvaus())) {
                kuvaus = koodiMetadata.getKuvaus();
            }
        }

        if (koodistoMetadata != null && StringUtils.isNotBlank(koodistoMetadata.getNimi())) {
            koodistoNimi = koodistoMetadata.getNimi();
        }

        Button deleteButton = new Button(I18N.getMessage("subKoodis.button.deleteRelation"));
        deleteButton.setStyleName(BaseTheme.BUTTON_LINK);
        deleteButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.showRemoveRelationView(isChild, koodi, relatedKoodi, SubKoodis.this, suhteenTyyppi);
                table.setPageLength(table.getItemIds().size() + 1);
                table.requestRepaint();
            }
        });
        deleteButton.setVisible(isEditable);

        Object[] tableLine = new Object[] { koodistoNimi, nimi, kuvaus, deleteButton };
        table.addItem(tableLine, relatedKoodi);
        table.setPageLength(table.getItemIds().size() + 1);
        table.requestRepaint();
    }

    public void buttonClick(ClickEvent event) {
        if (this.toggleLayout.isVisible()) {
            this.toggleLayout.setVisible(false);
        } else {
            this.toggleLayout.setVisible(true);
        }
    }

    public void refresh() {
        refresh(koodi);
    }

    public void refresh(KoodiType ko) {
        this.koodi = presenter.getKoodiByUriAndVersio(ko.getKoodiUri(), ko.getVersio());

        boolean isEditable = presenter.userCanEditKoodi(koodi) && presenter.isKoodiEditable(koodi);

        editButton.setVisible(isEditable);

        List<KoodiType> subCodes = presenter.listKoodiByRelation(koodi.getKoodiUri(), koodi.getVersio(), this.isChild, this.suhteenTyyppi);

        table.removeAllItems();
        Set<String> koodistoUris = new HashSet<String>();
        for (KoodiType k : subCodes) {
            koodistoUris.add(k.getKoodisto().getKoodistoUri());
        }

        Map<String, KoodistoType> koodistoMap = presenter.getKoodistosByUrisMap(koodistoUris);

        for (KoodiType relatedKoodi : subCodes) {
            addKoodiToTable(koodi, relatedKoodi, koodistoMap, isEditable);
        }
        table.setPageLength(table.getItemIds().size() + 1);
    }

    public VerticalLayout getToggleLayout() {
        return toggleLayout;
    }

    public Button getEditButton() {
        return editButton;
    }
}
