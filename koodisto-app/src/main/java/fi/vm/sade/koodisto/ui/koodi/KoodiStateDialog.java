package fi.vm.sade.koodisto.ui.koodi;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;

/**
 * 
 * @author
 * 
 */

@SuppressWarnings("serial")
public class KoodiStateDialog extends VerticalLayout {

    private KoodistoPresenter presenter;
    private KoodiType koodi;

    public KoodiStateDialog(KoodistoPresenter presenter) {
        this.presenter = presenter;
        initialize();
    }

    private void initialize() {
        addComponent(new Label(I18N.getMessage("koodiStateDialog.confirmationMessage")));

        Button confirmButton = new Button(I18N.getMessage("koodiStateDialog.confirmButton.caption"));
        confirmButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                presenter.koodiStateChange(koodi);
            }
        });
        addComponent(confirmButton);

        Button cancelButton = new Button(I18N.getMessage("koodiStateDialog.cancelButton.caption"));
        cancelButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.cancelKoodiStateChange(koodi);
            }
        });
        addComponent(cancelButton);
    }

    public void refresh(KoodiType koodi) {
        this.koodi = koodi;
    }
}
