package fi.vm.sade.koodisto.ui.koodi;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.ui.koodi.form.KoodiForm;
import fi.vm.sade.vaadin.Oph;

@SuppressWarnings("serial")
public class KoodiCreate extends VerticalLayout {
    private KoodistoPresenter presenter;
    private KoodiForm koodiForm;
    private KoodistoType koodisto;

    public KoodiCreate(KoodistoPresenter presenter) {
        this.presenter = presenter;
        setMargin(true);
        initialize();
    }

    private void initialize() {

        koodiForm = new KoodiForm(presenter);
        addComponent(koodiForm);

        Button cancelButton = new Button(I18N.getMessage("koodiCreate.button.cancel"));
        cancelButton.setStyleName(Oph.BUTTON_DEFAULT);
        cancelButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.cancelKoodiCreation();
            }
        });

        Button saveButton = new Button(I18N.getMessage("koodiCreate.button.save"));
        saveButton.setStyleName(Oph.BUTTON_PRIMARY);
        saveButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (koodiForm.validate()) {
                    KoodiType newKoodi = koodiForm.commit();
                    presenter.addKoodi(koodisto, newKoodi);
                }
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveButton);
        addComponent(buttonLayout);
        setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
    }

    public KoodiForm getKoodiForm() {
        return koodiForm;
    }

    public void refresh(KoodistoType koodisto) {
        this.koodisto = koodisto;
        KoodiType koodi = new KoodiType();
        koodi.setTila(TilaType.LUONNOS);
        koodi.setVersio(1);
        KoodistoItemType koodistoItem = new KoodistoItemType();
        koodistoItem.getKoodistoVersio().add(koodisto.getVersio());
        koodistoItem.setKoodistoUri(koodisto.getKoodistoUri());
        koodi.setKoodisto(koodistoItem);
        koodiForm.refresh(koodi, true);
    }
}
