package fi.vm.sade.koodisto.ui.koodi;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.util.KoodistoHelper;

/**
 * 
 * @author jteuho
 * 
 */

@SuppressWarnings("serial")
public class SubKoodiDeleteDialog extends VerticalLayout {

    private Label infoText;

    private KoodistoPresenter presenter;

    private boolean isChild;
    private KoodiType koodi;
    private KoodiType relatedKoodi;
    private SubKoodis source;
    private SuhteenTyyppiType suhteenTyyppi;

    public SubKoodiDeleteDialog(KoodistoPresenter presenter) {
        this.presenter = presenter;
        initialize();
    }

    private void initialize() {

        infoText = new Label();
        addComponent(infoText);

        Button deleteButton = new Button(I18N.getMessage("removeRelationDialog.deleteButton.caption"));
        deleteButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                KoodiUriAndVersioType ylakoodi = null;
                KoodiUriAndVersioType alakoodi = null;

                if (isChild) {
                    ylakoodi = koodiToKoodiUriAndVersio(relatedKoodi);
                    alakoodi = koodiToKoodiUriAndVersio(koodi);
                } else {
                    ylakoodi = koodiToKoodiUriAndVersio(koodi);
                    alakoodi = koodiToKoodiUriAndVersio(relatedKoodi);
                }

                presenter.removeRelation(source, ylakoodi, alakoodi, suhteenTyyppi);
            }

            private KoodiUriAndVersioType koodiToKoodiUriAndVersio(KoodiType koodi) {
                KoodiUriAndVersioType koodiAndVersio = new KoodiUriAndVersioType();
                koodiAndVersio.setKoodiUri(koodi.getKoodiUri());
                koodiAndVersio.setVersio(koodi.getVersio());
                return koodiAndVersio;
            }
        });
        addComponent(deleteButton);

        Button cancelButton = new Button(I18N.getMessage("removeRelationDialog.cancelButton.caption"));
        cancelButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.cancelRelationRemove();
            }
        });
        addComponent(cancelButton);
    }

    public void refresh(boolean isChild, KoodiType koodi, KoodiType relatedKoodi, SubKoodis source, SuhteenTyyppiType suhteenTyyppi) {
        this.isChild = isChild;
        this.koodi = koodi;
        this.relatedKoodi = relatedKoodi;
        this.source = source;
        this.suhteenTyyppi = suhteenTyyppi;

        KieliType kieli = KoodistoHelper.getKieliForLocale(I18N.getLocale());
        String koodiName = KoodistoHelper.createNameForKoodiVersio(koodi, kieli);
        String relatedKoodiName = KoodistoHelper.createNameForKoodiVersio(relatedKoodi, kieli);

        infoText.setValue(I18N.getMessage("removeRelationDialog.confirmationMessage", koodiName, relatedKoodiName, suhteenTyyppi.toString()));
    }
}
