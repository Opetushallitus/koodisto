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
 * @author twillberg
 * 
 */

@SuppressWarnings("serial")
public class KoodiDeleteDialog extends VerticalLayout {

    private KoodistoPresenter presenter;
    private KoodiType koodi;

    public KoodiDeleteDialog(KoodistoPresenter presenter) {
        this.presenter = presenter;
        initialize();
    }

    private void initialize() {
        addComponent(new Label(I18N.getMessage("koodiDeleteDialog.confirmationMessage")));

        Button deleteButton = new Button(I18N.getMessage("koodiDeleteDialog.deleteButton.caption"));
        deleteButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                presenter.deleteKoodi(koodi);
            }
        });
        addComponent(deleteButton);

        Button cancelButton = new Button(I18N.getMessage("koodiDeleteDialog.cancelButton.caption"));
        cancelButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.cancelKoodiDelete();
            }
        });
        addComponent(cancelButton);
    }

    public void refresh(KoodiType koodi) {
        this.koodi = koodi;
    }
}
