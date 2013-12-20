/**
 *
 */
package fi.vm.sade.koodisto.ui.koodi.form;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.message.ConfirmationMessage;
import fi.vm.sade.generic.ui.message.InfoMessage;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.ui.util.KoodistoValidator;
import fi.vm.sade.koodisto.ui.util.ValidatorUtil;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.ViewBoundForm;

import java.util.Arrays;
import java.util.List;

/**
 * @author tommiha
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class KoodiForm extends VerticalLayout {
    private ErrorMessage errorMessage;
    private InfoMessage infoMessage;
    private ConfirmationMessage confirmationMessage;

    private ViewBoundForm koodiViewBoundForm;
    private BeanItem<KoodiFormModel> beanItem;
    private KoodistoPresenter presenter;
    private KoodiType currentKoodi;

    public KoodiForm(KoodistoPresenter presenter) {
        this.presenter = presenter;
        this.setWidth("100%");
        errorMessage = new ErrorMessage();
        errorMessage.setWidth("100%");
        this.infoMessage = new InfoMessage();
        this.confirmationMessage = new ConfirmationMessage();
    }

    public void refresh(KoodiType koodi, boolean isNew) {

        this.currentKoodi = koodi;

        removeAllComponents();
        resetMessages();

        KoodiFormModel formModel = new KoodiFormModel();

        formModel.setKoodi(koodi);
        formModel.setPaivitysPvm(koodi.getPaivitysPvm() != null ? DateHelper.xmlCalToDate(koodi.getPaivitysPvm()) : null);
        formModel.setVoimassaAlkuPvm(koodi.getVoimassaAlkuPvm() != null ? DateHelper.xmlCalToDate(koodi.getVoimassaAlkuPvm()) : null);
        formModel.setVoimassaLoppuPvm(koodi.getVoimassaLoppuPvm() != null ? DateHelper.xmlCalToDate(koodi.getVoimassaLoppuPvm()) : null);

        KoodiMetadataType fiMetadata = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KieliType.FI);
        if (fiMetadata == null) {
            fiMetadata = new KoodiMetadataType();
            fiMetadata.setKieli(KieliType.FI);
        }
        formModel.setFiMetadata(fiMetadata);

        KoodiMetadataType svMetadata = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KieliType.SV);
        if (svMetadata == null) {
            svMetadata = new KoodiMetadataType();
            svMetadata.setKieli(KieliType.SV);
        }
        formModel.setSvMetadata(svMetadata);

        KoodiMetadataType enMetadata = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KieliType.EN);
        if (enMetadata == null) {
            enMetadata = new KoodiMetadataType();
            enMetadata.setKieli(KieliType.EN);
        }
        formModel.setEnMetadata(enMetadata);

        KoodiFormLayout koodiEditForm = new KoodiFormLayout(isNew, koodi.getTila(), presenter.getKoodistoResourceUri(koodi.getKoodisto().getKoodistoUri()));
        koodiViewBoundForm = new ValidatingViewBoundForm(koodiEditForm);
        beanItem = initializeForm(formModel);
        koodiViewBoundForm.setItemDataSource(beanItem);
        koodiViewBoundForm.setWidth("100%");
        koodiViewBoundForm.setValidationVisibleOnCommit(false);
        koodiViewBoundForm.setValidationVisible(false);

        addComponent(koodiViewBoundForm);
        addComponent(errorMessage);
        addComponent(infoMessage);
        addComponent(confirmationMessage);

        if (!isNew && (!presenter.userCanEditKoodi(koodi) || !presenter.isKoodiEditable(koodi))) {
            koodiEditForm.setFieldsReadOnly(true);
        }
    }

    public boolean validate() {
        try {
            resetMessages();
            koodiViewBoundForm.validate();
        } catch (InvalidValueException e) {
            errorMessage.addError(e);
        }
        KoodiFormModel koodiForm = beanItem.getBean();

        for (String error : KoodistoValidator.validate(koodiForm.getFiMetadata(), koodiForm.getSvMetadata(), koodiForm.getEnMetadata())) {
            errorMessage.addError(error);
        }

        if (koodiForm.getVoimassaAlkuPvm() != null
                && !ValidatorUtil.dateIsEitherNullOrAfterDate(koodiForm.getVoimassaAlkuPvm(), koodiForm.getVoimassaLoppuPvm())) {
            errorMessage.addError(I18N.getMessage("koodiEditForm.validate.voimassaLoppuPvm"));
        }

        return !errorMessage.hasErrors();
    }

    public KoodiType commit() {
        if (!validate()) {
            return null;
        }

        KoodiFormModel koodiForm = beanItem.getBean();
        KoodiType koodi = koodiForm.getKoodi();
        koodi.setPaivitysPvm(koodiForm.getPaivitysPvm() != null ? DateHelper.DateToXmlCal(koodiForm.getPaivitysPvm()) : null);
        koodi.setVoimassaAlkuPvm(koodiForm.getVoimassaAlkuPvm() != null ? DateHelper.DateToXmlCal(koodiForm.getVoimassaAlkuPvm()) : null);
        koodi.setVoimassaLoppuPvm(koodiForm.getVoimassaLoppuPvm() != null ? DateHelper.DateToXmlCal(koodiForm.getVoimassaLoppuPvm()) : null);

        koodi.getMetadata().clear();
        koodi.getMetadata().addAll(koodiForm.getMetadatasWithFieldsFilled());
        return koodi;
    }

    private BeanItem<KoodiFormModel> initializeForm(KoodiFormModel formModel) {
        final BeanItem<KoodiFormModel> beanItem = new BeanItem<KoodiFormModel>(formModel);
        List<String> properties = Arrays.asList("koodi.koodiUri", "koodi.paivitysPvm", "koodi.tila", "koodi.versio", "koodi.voimassaAlkuPvm",
                "koodi.voimassaLoppuPvm", "koodi.koodiArvo", "fiMetadata.nimi", "fiMetadata.lyhytNimi", "fiMetadata.kuvaus", "fiMetadata.kasite",
                "fiMetadata.huomioitavaKoodi", "fiMetadata.kayttoohje", "fiMetadata.sisaltaaMerkityksen", "fiMetadata.eiSisallaMerkitysta",
                "fiMetadata.sisaltaaKoodiston", "svMetadata.nimi", "svMetadata.lyhytNimi", "svMetadata.kuvaus", "svMetadata.kasite",
                "svMetadata.huomioitavaKoodi", "svMetadata.kayttoohje", "svMetadata.sisaltaaMerkityksen", "svMetadata.eiSisallaMerkitysta",
                "svMetadata.sisaltaaKoodiston", "enMetadata.nimi", "enMetadata.lyhytNimi", "enMetadata.kuvaus", "enMetadata.kasite",
                "enMetadata.huomioitavaKoodi", "enMetadata.kayttoohje", "enMetadata.sisaltaaMerkityksen", "enMetadata.eiSisallaMerkitysta",
                "enMetadata.sisaltaaKoodiston");
        for (String property : properties) {
            beanItem.addItemProperty(property, new NestedMethodProperty(formModel, property));
        }
        return beanItem;
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

    public KoodiType getCurrentKoodi() {
        return this.currentKoodi;
    }
}
