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
 * @author twillberg
 * 
 */

@SuppressWarnings("serial")
public class KoodistoDeleteDialog extends VerticalLayout {

    private KoodistoPresenter presenter;
    private KoodistoType koodisto;

    public KoodistoDeleteDialog(KoodistoPresenter presenter) {
        this.presenter = presenter;
        initialize();
    }

    public void refresh(KoodistoType koodisto) {
        this.koodisto = koodisto;
    }

    private void initialize() {
        Button cancelButton = new Button(I18N.getMessage("koodistoDeleteDialog.cancelButton.caption"));
        cancelButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.cancelKoodistoDelete();
            }
        });
        addComponent(new Label(I18N.getMessage("koodistoDeleteDialog.confirmationMessage")));
        Button deleteButton = new Button(I18N.getMessage("koodistoDeleteDialog.deleteButton.caption"));
        deleteButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                presenter.deleteKoodisto(koodisto);
            }
        });
        addComponent(deleteButton);
        addComponent(cancelButton);

    }
}
