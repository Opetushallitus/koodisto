/**
 * 
 */
package fi.vm.sade.koodisto.ui.koodi;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.ui.koodi.form.KoodiForm;
import fi.vm.sade.vaadin.Oph;

/**
 * @author tommiha
 * 
 */
@SuppressWarnings("serial")
public class KoodiEdit extends VerticalLayout {

    private KoodiForm koodiForm;
    private Button saveButton;
    private KoodistoPresenter presenter;

    public KoodiEdit(KoodistoPresenter presenter) {
        this.presenter = presenter;
        this.setMargin(true);
        initialize();
    }

    private void initialize() {
        koodiForm = new KoodiForm(presenter);
        addComponent(koodiForm);

        saveButton = new Button(I18N.getMessage("koodiEdit.button.save"));
        saveButton.setStyleName(Oph.BUTTON_PRIMARY);
        addComponent(saveButton);
        setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);

        saveButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (koodiForm.validate()) {
                    KoodiType updatedKoodi = koodiForm.commit();
                    presenter.updateKoodi(updatedKoodi);
                }
            }
        });

    }

    public void showKoodi(KoodiType koodi) {
        koodiForm.refresh(koodi, false);
        saveButton.setVisible(presenter.userCanEditKoodi(koodi) && presenter.isKoodiEditable(koodi));
    }

    public KoodiForm getKoodiForm() {
        return koodiForm;
    }
}
