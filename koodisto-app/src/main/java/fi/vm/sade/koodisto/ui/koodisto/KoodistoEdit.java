package fi.vm.sade.koodisto.ui.koodisto;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.ui.koodisto.form.KoodistoForm;
import fi.vm.sade.vaadin.Oph;

/**
 * @author tommiha
 * 
 */
@SuppressWarnings("serial")
public class KoodistoEdit extends VerticalLayout {
    private KoodistoForm koodistoForm;

    private Button saveButton;
    private KoodistoPresenter presenter;

    public KoodistoEdit(KoodistoPresenter presenter) {
        this.presenter = presenter;
        this.setMargin(true);

        initialize();
    }

    private void initialize() {
        koodistoForm = new KoodistoForm(presenter);
        addComponent(koodistoForm);

        saveButton = new Button(I18N.getMessage("koodistoEdit.button.save"));
        addComponent(saveButton);
        setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
        saveButton.addStyleName(Oph.BUTTON_PRIMARY);
        saveButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (koodistoForm.validate()) {
                    KoodistoType updatedKoodisto = koodistoForm.commit();
                    presenter.updateKoodisto(updatedKoodisto);
                    presenter.setDataChanged(false);
                }
            }
        });

        presenter.setDataChanged(false);
    }

    public void showKoodisto(KoodistoType koodisto) {
        koodistoForm.refresh(koodisto, false);
        saveButton.setVisible(presenter.userCanEditKoodisto(koodisto) && presenter.isKoodistoEditable(koodisto));
    }

    public KoodistoForm getKoodistoForm() {
        return koodistoForm;
    }

}
