package fi.vm.sade.koodisto.ui.koodisto;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;

/**
 * 
 * @author
 * 
 */

@SuppressWarnings("serial")
public class KoodistoStateDialog extends VerticalLayout {

    private KoodistoPresenter presenter;
    private KoodistoType koodisto;

    public KoodistoStateDialog(KoodistoPresenter presenter) {
        this.presenter = presenter;
        initialize();
    }

    public void refresh(KoodistoType koodisto) {
        this.koodisto = koodisto;
    }

    private void initialize() {
        Button cancelButton = new Button(I18N.getMessage("koodistoStateDialog.cancelButton.caption"));
        cancelButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.cancelKoodistoStateChange(koodisto);
            }
        });
        addComponent(new Label(I18N.getMessage("koodistoStateDialog.confirmationMessage")));
        Button confirmButton = new Button(I18N.getMessage("koodistoStateDialog.confirmButton.caption"));
        confirmButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                presenter.koodistoStateChange(koodisto);
            }
        });
        addComponent(confirmButton);
        addComponent(cancelButton);

    }
}
