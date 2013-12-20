package fi.vm.sade.koodisto.ui.koodisto;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.ui.koodisto.form.KoodistoForm;
import fi.vm.sade.vaadin.Oph;

@SuppressWarnings("serial")
public class KoodistoCreate extends VerticalLayout {

    private KoodistoPresenter presenter;
    private KoodistoForm koodistoForm;
    private KoodistoRyhmaListType selectedKoodistoRyhma;

    public KoodistoCreate(KoodistoPresenter presenter) {
        this.setMargin(true);
        this.presenter = presenter;
        koodistoForm = new KoodistoForm(presenter);
        initialize();
    }

    public void refresh(KoodistoRyhmaListType selectedKoodistoRyhma) {
        // Let's set the default values for new koodisto.
        KoodistoType koodisto = new KoodistoType();
        koodisto.setTila(TilaType.LUONNOS);
        koodisto.setVersio(1);
        this.selectedKoodistoRyhma = selectedKoodistoRyhma;
        koodistoForm.refresh(koodisto, true);
    }

    private void initialize() {
        addComponent(koodistoForm);

        Button cancelButton = new Button(I18N.getMessage("koodistoCreate.button.cancel"));
        cancelButton.setStyleName(Oph.BUTTON_DEFAULT);
        cancelButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.cancelKoodistoCreation();
            }
        });

        Button saveButton = new Button(I18N.getMessage("koodistoCreate.button.save"));
        saveButton.setStyleName(Oph.BUTTON_PRIMARY);
        saveButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (koodistoForm.validate()) {
                    KoodistoType newKoodisto = koodistoForm.commit();
                    presenter.addKoodisto(selectedKoodistoRyhma, newKoodisto);
                    presenter.setDataChanged(false);
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

    public KoodistoForm getKoodistoForm() {
        return koodistoForm;
    }
}
