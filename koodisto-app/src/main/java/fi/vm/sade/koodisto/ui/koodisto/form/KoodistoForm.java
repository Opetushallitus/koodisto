package fi.vm.sade.koodisto.ui.koodisto.form;

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
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.ui.KoodistoApplication;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.ui.debug.DebugIdGenerator;
import fi.vm.sade.koodisto.ui.util.KoodistoValidator;
import fi.vm.sade.koodisto.ui.util.ValidatorUtil;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import org.vaadin.addon.formbinder.ViewBoundForm;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class KoodistoForm extends VerticalLayout {
    private ErrorMessage errorMessage;
    private InfoMessage infoMessage;
    private ConfirmationMessage confirmationMessage;

    private ViewBoundForm koodistoViewBoundForm;
    private BeanItem<KoodistoFormModel> beanItem;

    private KoodistoPresenter presenter;

    public KoodistoForm(KoodistoPresenter presenter) {
        this.presenter = presenter;

        this.setWidth("100%");
        this.errorMessage = new ErrorMessage();
        this.infoMessage = new InfoMessage();
        this.confirmationMessage = new ConfirmationMessage();
    }

    public void refresh(KoodistoType koodisto, KoodistoType latest) {

    }

    public void refresh(KoodistoType koodisto, boolean isNew) {
        removeAllComponents();
        resetMessages();

        KoodistoFormModel formModel = new KoodistoFormModel();
        formModel.setKoodisto(koodisto);

        formModel.setPaivitysPvm(koodisto.getPaivitysPvm() != null ? DateHelper.xmlCalToDate(koodisto.getPaivitysPvm()) : null);
        if (isNew) {
            formModel.setVoimassaAlkuPvm(new Date());
        } else {
            formModel.setVoimassaAlkuPvm(koodisto.getVoimassaAlkuPvm() != null ? DateHelper.xmlCalToDate(koodisto.getVoimassaAlkuPvm()) : null);
        }
        formModel.setVoimassaLoppuPvm(koodisto.getVoimassaLoppuPvm() != null ? DateHelper.xmlCalToDate(koodisto.getVoimassaLoppuPvm()) : null);

        KoodistoMetadataType fiMetadata = KoodistoHelper.getKoodistoMetadataForLanguage(koodisto, KieliType.FI);
        if (fiMetadata == null) {
            fiMetadata = new KoodistoMetadataType();
            fiMetadata.setKieli(KieliType.FI);
        }
        formModel.setFiMetadata(fiMetadata);

        KoodistoMetadataType svMetadata = KoodistoHelper.getKoodistoMetadataForLanguage(koodisto, KieliType.SV);
        if (svMetadata == null) {
            svMetadata = new KoodistoMetadataType();
            svMetadata.setKieli(KieliType.SV);
        }
        formModel.setSvMetadata(svMetadata);

        KoodistoMetadataType enMetadata = KoodistoHelper.getKoodistoMetadataForLanguage(koodisto, KieliType.EN);
        if (enMetadata == null) {
            enMetadata = new KoodistoMetadataType();
            enMetadata.setKieli(KieliType.EN);
        }
        formModel.setEnMetadata(enMetadata);

        KoodistoFormLayout koodistoFormLayout = new KoodistoFormLayout(isNew, presenter.getBaseUri());
        DebugIdGenerator.ensureDebugId(koodistoFormLayout);
        koodistoViewBoundForm = new ValidatingViewBoundForm(koodistoFormLayout);
        beanItem = initializeForm(formModel);

        koodistoViewBoundForm.setItemDataSource(beanItem);
        koodistoViewBoundForm.setWidth("100%");

        addComponent(koodistoViewBoundForm);
        addComponent(errorMessage);
        addComponent(infoMessage);
        addComponent(confirmationMessage);

        if (!isNew && (!presenter.userCanEditKoodisto(koodisto) || !presenter.isKoodistoEditable(koodisto))) {
            koodistoFormLayout.setFieldsReadOnly(true);
        }

        KoodistoApplication.getInstance().getPresenter().setDataChanged(false);
        koodistoFormLayout.startMonitoringValueChanges();
    }

    public boolean validate() {
        try {
            resetMessages();
            koodistoViewBoundForm.validate();
        } catch (InvalidValueException e) {
            errorMessage.addError(e);
        }

        KoodistoFormModel koodistoForm = beanItem.getBean();

        for (String error : KoodistoValidator.validate(koodistoForm.getFiMetadata(), koodistoForm.getSvMetadata(), koodistoForm.getEnMetadata())) {
            errorMessage.addError(error);
        }

        if (koodistoForm.getVoimassaAlkuPvm() != null
                && !ValidatorUtil.dateIsEitherNullOrAfterDate(koodistoForm.getVoimassaAlkuPvm(), koodistoForm.getVoimassaLoppuPvm())) {
            errorMessage.addError(I18N.getMessage("koodistoEditForm.validate.voimassaLoppuPvm"));
        }

        return !errorMessage.hasErrors();

    }

    public KoodistoType commit() {
        if (!validate()) {
            return null;
        }
        koodistoViewBoundForm.commit();

        KoodistoFormModel koodistoForm = beanItem.getBean();
        KoodistoType koodisto = koodistoForm.getKoodisto();
        koodisto.setPaivitysPvm(koodistoForm.getPaivitysPvm() != null ? DateHelper.DateToXmlCal(koodistoForm.getPaivitysPvm()) : null);
        koodisto.setVoimassaAlkuPvm(koodistoForm.getVoimassaAlkuPvm() != null ? DateHelper.DateToXmlCal(koodistoForm.getVoimassaAlkuPvm()) : null);
        koodisto.setVoimassaLoppuPvm(koodistoForm.getVoimassaLoppuPvm() != null ? DateHelper.DateToXmlCal(koodistoForm.getVoimassaLoppuPvm()) : null);

        koodisto.getMetadataList().clear();
        koodisto.getMetadataList().addAll(koodistoForm.getMetadatasWithFieldsFilled());
        return koodisto;
    }

    private BeanItem<KoodistoFormModel> initializeForm(KoodistoFormModel formModel) {
        final BeanItem<KoodistoFormModel> beanItem = new BeanItem<KoodistoFormModel>(formModel);
        List<String> properties = Arrays.asList("koodisto.koodistoUri", "koodisto.lukittu", "koodisto.omistaja", "koodisto.paivitysPvm", "koodisto.tila",
                "koodisto.omistaja", "koodisto.organisaatioOid", "koodisto.versio", "koodisto.voimassaAlkuPvm", "koodisto.voimassaLoppuPvm", "fiMetadata.nimi",
                "fiMetadata.kuvaus", "fiMetadata.kasite", "fiMetadata.kayttoohje", "fiMetadata.kohdealue", "fiMetadata.kohdealueenOsaAlue",
                "fiMetadata.sitovuustaso", "fiMetadata.tarkentaaKoodistoa", "fiMetadata.huomioitavaKoodisto", "fiMetadata.koodistonLahde",
                "fiMetadata.toimintaymparisto", "svMetadata.nimi", "svMetadata.kuvaus", "svMetadata.kasite", "svMetadata.kayttoohje", "svMetadata.kohdealue",
                "svMetadata.kohdealueenOsaAlue", "svMetadata.sitovuustaso", "svMetadata.tarkentaaKoodistoa", "svMetadata.huomioitavaKoodisto",
                "svMetadata.koodistonLahde", "svMetadata.toimintaymparisto", "enMetadata.nimi", "enMetadata.kuvaus", "enMetadata.kasite",
                "enMetadata.kayttoohje", "enMetadata.kohdealue", "enMetadata.kohdealueenOsaAlue", "enMetadata.sitovuustaso", "enMetadata.tarkentaaKoodistoa",
                "enMetadata.huomioitavaKoodisto", "enMetadata.koodistonLahde", "enMetadata.toimintaymparisto");
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
}