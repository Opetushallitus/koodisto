package fi.vm.sade.koodisto.ui.koodi;

import fi.vm.sade.generic.ui.message.ConfirmationMessage;
import fi.vm.sade.generic.ui.message.InfoMessage;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import org.apache.commons.lang.StringUtils;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.vaadin.Oph;

/**
 * @author tommiha
 * @author twillberg
 * 
 */
@SuppressWarnings("serial")
public class KoodiDetails extends VerticalLayout {
    private static final String FULL = "100%";
    private ErrorMessage errorMessage;
    private InfoMessage infoMessage;
    private ConfirmationMessage confirmationMessage;

    private Label kuvausLabel;

    private Button deleteKoodiButton;

    private KoodiType koodi;

    private Label uriLabel;

    private Label nimiLabel;
    private KoodistoPresenter presenter;

    public KoodiDetails(KoodistoPresenter presenter) {
        this.presenter = presenter;
        setWidth(FULL);
        setHeight(FULL);
        setSpacing(true);

        this.errorMessage = new ErrorMessage();
        this.infoMessage = new InfoMessage();
        this.confirmationMessage = new ConfirmationMessage();

        initialize();
    }

    private void initialize() {
        uriLabel = new Label();
        addComponent(uriLabel);
        uriLabel.setWidth(null);
        setComponentAlignment(uriLabel, Alignment.MIDDLE_RIGHT);

        nimiLabel = new Label();
        nimiLabel.setStyleName(Oph.LABEL_H2);

        kuvausLabel = new Label();
        addComponent(nimiLabel);
        addComponent(kuvausLabel);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setMargin(true, false, true, false);
        deleteKoodiButton = new Button(I18N.getMessage("koodiDetails.button.deleteKoodi"));
        deleteKoodiButton.addStyleName(Oph.BUTTON_SMALL);
        deleteKoodiButton.addListener(new DeleteKoodiButtonEventListener());
        buttonLayout.addComponent(deleteKoodiButton);
        addComponent(buttonLayout);

        addComponent(errorMessage);
        addComponent(infoMessage);
        addComponent(confirmationMessage);
    }

    public void showKoodi(KoodiType koodi) {
        resetMessages();

        deleteKoodiButton.setVisible(presenter.userCanDeleteKoodi(koodi));

        KoodiMetadataType metadata = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KoodistoHelper.getKieliForLocale(I18N.getLocale()));

        uriLabel.setValue(presenter.getKoodiResourceUri(koodi.getKoodisto().getKoodistoUri(), koodi.getKoodiUri()));

        String nimi = "N/A (" + koodi.getKoodiArvo() + ")";
        String kuvaus = "";

        if (metadata == null) {
            metadata = KoodistoHelper.getKoodiMetadataForAnyLanguage(koodi);
        }

        if (metadata != null) {
            if (StringUtils.isNotBlank(metadata.getNimi())) {
                nimi = metadata.getNimi() + " (" + koodi.getKoodiArvo() + ")";
            }
            if (StringUtils.isNotBlank(metadata.getKuvaus())) {
                kuvaus = metadata.getKuvaus();
            }
        }

        nimiLabel.setValue(nimi);
        kuvausLabel.setValue(kuvaus);
        deleteKoodiButton.setEnabled(TilaType.PASSIIVINEN.equals(koodi.getTila()));

        this.koodi = koodi;
    }

    private class DeleteKoodiButtonEventListener implements ClickListener {

        public void buttonClick(final ClickEvent event) {
            presenter.showDeleteKoodiView(koodi);
        }

    }

    public Button getDeleteKoodiButton() {
        return deleteKoodiButton;
    }

    private void resetMessages() {
        this.errorMessage.resetErrors();
        this.infoMessage.resetMessages();
        this.confirmationMessage.resetMessages();
    }

    public void addErrorMessage(String errorMessage) {
        resetMessages();
        this.errorMessage.addError(errorMessage);
    }

    public void addInfoMessage(String infoMessage) {
        resetMessages();
        this.infoMessage.addMessage(infoMessage);
    }

    public void addConfirmationMessage(String confirmationMessage) {
        resetMessages();
        this.confirmationMessage.addMessage(confirmationMessage);
    }
}
