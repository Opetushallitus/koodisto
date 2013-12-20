/**
 *
 */
package fi.vm.sade.koodisto.ui.koodisto.form;

import com.vaadin.data.Property;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.LinkedFieldComponent;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.common.util.FieldLengths;
import fi.vm.sade.koodisto.ui.KoodistoApplication;
import fi.vm.sade.koodisto.ui.common.form.AbstractForm;
import fi.vm.sade.organisaatio.ui.widgets.simple.OrganisaatioComponent;
import fi.vm.sade.organisaatio.ui.widgets.simple.SimpleOrganisaatioWidgetFactory;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author tommiha
 */
@SuppressWarnings("serial")
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class KoodistoFormLayout extends AbstractForm {
    @PropertyId("koodisto.koodistoUri")
    private TextField koodistoUri;

    @PropertyId("paivitysPvm")
    private DateField paivitysPvm;

    @NotNull(message = "{koodistoEditForm.validate.tila}")
    @PropertyId("koodisto.tila")
    private Select tila;

    @PropertyId("koodisto.omistaja")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodistoEditForm.validate.length.omistaja}")
    private TextField omistaja;

    @NotNull(message = "{koodistoEditForm.validate.organisaatioOid}")
    @NotBlank(message = "{koodistoEditForm.validate.organisaatioOid}")
    @PropertyId("koodisto.organisaatioOid")
    private OrganisaatioComponent organisaatioOid;

    @Min(value = 1, message = "{koodistoEditForm.validate.versio}")
    @NotNull(message = "{koodistoEditForm.validate.versio}")
    @PropertyId("koodisto.versio")
    private TextField versio;

    @PropertyId("voimassaAlkuPvm")
    @NotNull(message = "{koodistoEditForm.validate.voimassaAlkuPvm}")
    private DateField voimassaAlkuPvm;

    @PropertyId("voimassaLoppuPvm")
    private DateField voimassaLoppuPvm;

    // METADATA FI
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.nimiFi")
    @PropertyId("fiMetadata.nimi")
    private TextField nimiFi;

    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kuvausFi")
    @PropertyId("fiMetadata.kuvaus")
    private TextArea kuvausFi;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kasiteFi")
    @PropertyId("fiMetadata.kasite")
    private TextField kasiteFi;

    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kayttoohjeFi")
    @PropertyId("fiMetadata.kayttoohje")
    private TextArea kayttoohjeFi;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kohdealueFi")
    @PropertyId("fiMetadata.kohdealue")
    private TextField kohdealueFi;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kohdealueenOsaalueFi")
    @PropertyId("fiMetadata.kohdealueenOsaAlue")
    private TextField kohdealueenOsaAlueFi;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.sitovuustasoFi")
    @PropertyId("fiMetadata.sitovuustaso")
    private TextField sitovuustasoFi;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.tarkentaaKoodistoaFi")
    @PropertyId("fiMetadata.tarkentaaKoodistoa")
    private TextField tarkentaaKoodistoaFi;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.huomioitavaKoodistoFi")
    @PropertyId("fiMetadata.huomioitavaKoodisto")
    private TextField huomioitavaKoodistoFi;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.koodistonLahdeFi")
    @PropertyId("fiMetadata.koodistonLahde")
    private TextField koodistonLahdeFi;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.toimintaymparistoFi")
    @PropertyId("fiMetadata.toimintaymparisto")
    private TextField toimintaymparistoFi;

    // METADATA SV
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.nimiSv")
    @PropertyId("svMetadata.nimi")
    private TextField nimiSv;

    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kuvausSv")
    @PropertyId("svMetadata.kuvaus")
    private TextArea kuvausSv;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kasiteSv")
    @PropertyId("svMetadata.kasite")
    private TextField kasiteSv;

    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kayttoohjeSv")
    @PropertyId("svMetadata.kayttoohje")
    private TextArea kayttoohjeSv;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kohdealueSv")
    @PropertyId("svMetadata.kohdealue")
    private TextField kohdealueSv;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kohdealueenOsaalueSv")
    @PropertyId("svMetadata.kohdealueenOsaAlue")
    private TextField kohdealueenOsaAlueSv;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.sitovuustasoSv")
    @PropertyId("svMetadata.sitovuustaso")
    private TextField sitovuustasoSv;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.tarkentaaKoodistoaSv")
    @PropertyId("svMetadata.tarkentaaKoodistoa")
    private TextField tarkentaaKoodistoaSv;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.huomioitavaKoodistoSv")
    @PropertyId("svMetadata.huomioitavaKoodisto")
    private TextField huomioitavaKoodistoSv;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.koodistonLahdeSv")
    @PropertyId("svMetadata.koodistonLahde")
    private TextField koodistonLahdeSv;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.toimintaymparistoSv")
    @PropertyId("svMetadata.toimintaymparisto")
    private TextField toimintaymparistoSv;

    // METADATA EN
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.nimiEn")
    @PropertyId("enMetadata.nimi")
    private TextField nimiEn;

    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kuvausEn")
    @PropertyId("enMetadata.kuvaus")
    private TextArea kuvausEn;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kasiteEn")
    @PropertyId("enMetadata.kasite")
    private TextField kasiteEn;

    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kayttoohjeEn")
    @PropertyId("enMetadata.kayttoohje")
    private TextArea kayttoohjeEn;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kohdealueEn")
    @PropertyId("enMetadata.kohdealue")
    private TextField kohdealueEn;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.kohdealueenOsaalueEn")
    @PropertyId("enMetadata.kohdealueenOsaAlue")
    private TextField kohdealueenOsaAlueEn;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.sitovuustasoEn")
    @PropertyId("enMetadata.sitovuustaso")
    private TextField sitovuustasoEn;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.tarkentaaKoodistoaEn")
    @PropertyId("enMetadata.tarkentaaKoodistoa")
    private TextField tarkentaaKoodistoaEn;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.huomioitavaKoodistoEn")
    @PropertyId("enMetadata.huomioitavaKoodisto")
    private TextField huomioitavaKoodistoEn;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.koodistonLahdeEn")
    @PropertyId("enMetadata.koodistonLahde")
    private TextField koodistonLahdeEn;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "koodistoEditForm.validate.length.toimintaymparistoEn")
    @PropertyId("enMetadata.toimintaymparisto")
    private TextField toimintaymparistoEn;

    private LinkedFieldComponent nimiLinkedField;
    private LinkedFieldComponent kuvausLinkedField;
    private LinkedFieldComponent kayttoohjeLinkedField;
    private LinkedFieldComponent kasiteLinkedField;
    private LinkedFieldComponent kohdealueLinkedField;
    private LinkedFieldComponent kohdealueenOsaalueLinkedField;
    private LinkedFieldComponent toimintaymparistoLinkedField;
    private LinkedFieldComponent koodistonLahdeLinkedField;
    private LinkedFieldComponent sitovuustasoLinkedField;
    private LinkedFieldComponent tarkentaaKoodistoaLinkedField;
    private LinkedFieldComponent huomioitavaKoodistoLinkedField;

    public KoodistoFormLayout(boolean isNew, String baseUri) {
        super(isNew);
        initComponents();
        GridLayout fieldLayout = createFieldLayout();
        addComponent(fieldLayout);

        if (!isNew) {
            addStateSelect(fieldLayout, I18N.getMessage("koodistoEditForm.koodisto.state"), tila);
        }

        nimiLinkedField = linkedFieldHelper.createLinkedField(nimiFi, nimiSv, nimiEn);
        addLabelAndContent(fieldLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.name")), nimiLinkedField);
        nimiLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        if (!isNew) {
            addUriLayout(fieldLayout, baseUri, koodistoUri, I18N.getMessage("koodistoEditForm.koodisto.uri"));
            addVersio(fieldLayout, versio, I18N.getMessage("koodistoEditForm.koodisto.versio"));
        }

        addValidityLayout(fieldLayout, voimassaAlkuPvm, voimassaLoppuPvm, I18N.getMessage("koodistoEditForm.koodisto.validity"));

        kuvausLinkedField = linkedFieldHelper.createLinkedField(kuvausFi, kuvausSv, kuvausEn);
        addLabelAndContent(fieldLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.description")), kuvausLinkedField);
        kuvausLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        addLabelAndContent(fieldLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.owner")), omistaja, Alignment.MIDDLE_RIGHT);
        addLabelAndContent(fieldLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.organisaatio")), organisaatioOid);

        final Button hideShowExtendedMetadata = new Button(I18N.getMessage("koodistoEditForm.koodisto.showExtendedMetadata"));
        hideShowExtendedMetadata.setStyleName(BaseTheme.BUTTON_LINK);

        addComponent(hideShowExtendedMetadata);
        setComponentAlignment(hideShowExtendedMetadata, Alignment.MIDDLE_LEFT);

        final GridLayout extendedMetaLayout = createExtendedMetadataLayout();
        addComponent(extendedMetaLayout);

        hideShowExtendedMetadata.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (extendedMetaLayout.isVisible()) {
                    extendedMetaLayout.setVisible(false);
                    hideShowExtendedMetadata.setCaption(I18N.getMessage("koodistoEditForm.koodisto.showExtendedMetadata"));
                } else {
                    extendedMetaLayout.setVisible(true);
                    hideShowExtendedMetadata.setCaption(I18N.getMessage("koodistoEditForm.koodisto.hideExtendedMetadata"));
                }
            }
        });

        kayttoohjeLinkedField = linkedFieldHelper.createLinkedField(kayttoohjeFi, kayttoohjeSv, kayttoohjeEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.instructions")), kayttoohjeLinkedField);
        kayttoohjeLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        kohdealueLinkedField = linkedFieldHelper.createLinkedField(kohdealueFi, kohdealueSv, kohdealueEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.targetArea")), kohdealueLinkedField);
        kohdealueLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        kohdealueenOsaalueLinkedField = linkedFieldHelper.createLinkedField(kohdealueenOsaAlueFi, kohdealueenOsaAlueSv, kohdealueenOsaAlueEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.targetAreaPart")), kohdealueenOsaalueLinkedField);
        kohdealueenOsaalueLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        kasiteLinkedField = linkedFieldHelper.createLinkedField(kasiteFi, kasiteSv, kasiteEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.concept")), kasiteLinkedField);
        kasiteLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        toimintaymparistoLinkedField = linkedFieldHelper.createLinkedField(toimintaymparistoFi, toimintaymparistoSv, toimintaymparistoEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.operationalEnvironment")), toimintaymparistoLinkedField);
        toimintaymparistoLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        koodistonLahdeLinkedField = linkedFieldHelper.createLinkedField(koodistonLahdeFi, koodistonLahdeSv, koodistonLahdeEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.koodistoSource")), koodistonLahdeLinkedField);
        koodistonLahdeLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        tarkentaaKoodistoaLinkedField = linkedFieldHelper.createLinkedField(tarkentaaKoodistoaFi, tarkentaaKoodistoaSv, tarkentaaKoodistoaEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.specifiesKoodisto")), tarkentaaKoodistoaLinkedField);
        tarkentaaKoodistoaLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        huomioitavaKoodistoLinkedField = linkedFieldHelper.createLinkedField(huomioitavaKoodistoFi, huomioitavaKoodistoSv, huomioitavaKoodistoEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.koodistoToTakeNoticeOf")), huomioitavaKoodistoLinkedField);
        huomioitavaKoodistoLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        sitovuustasoLinkedField = linkedFieldHelper.createLinkedField(sitovuustasoFi, sitovuustasoSv, sitovuustasoEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodistoEditForm.koodisto.validityLevel")), sitovuustasoLinkedField);
        sitovuustasoLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        addComponent(createLastUpdatedLayout(new Label(I18N.getMessage("koodistoEditForm.koodisto.previousUpdate")), paivitysPvm));

        nimiLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());
        kuvausLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());
        kayttoohjeLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());
        kasiteLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());
        kohdealueLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());
        kohdealueenOsaalueLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());
        toimintaymparistoLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());
        koodistonLahdeLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());
        sitovuustasoLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());
        tarkentaaKoodistoaLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());
        huomioitavaKoodistoLinkedField.addCheckboxValueChangeListener(new ValueChangeListener());

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    private void initComponents() {
        // COMMON VALUES
        koodistoUri = createTextField();
        koodistoUri.setReadOnly(!isNew);
        koodistoUri.setWidth("100%");

        versio = createTextField();
        versio.setWidth(4, Sizeable.UNITS_EM);
        versio.setReadOnly(true);
        versio.setEnabled(false);

        voimassaAlkuPvm = new DateField();
        voimassaAlkuPvm.setResolution(DateField.RESOLUTION_DAY);
        voimassaAlkuPvm.setDateFormat("dd.MM.yyyy");
        voimassaAlkuPvm.setLocale(LocaleContextHolder.getLocale());
        voimassaAlkuPvm.setWidth("100%");
        voimassaAlkuPvm.setParseErrorMessage(I18N.getMessage("common.unparseableDate"));
        voimassaAlkuPvm.addListener(new ValueChangeListener());
        voimassaAlkuPvm.setImmediate(true);

        voimassaLoppuPvm = new DateField();
        voimassaLoppuPvm.setResolution(DateField.RESOLUTION_DAY);
        voimassaLoppuPvm.setDateFormat("dd.MM.yyyy");
        voimassaLoppuPvm.setLocale(LocaleContextHolder.getLocale());
        voimassaLoppuPvm.setWidth("100%");
        voimassaLoppuPvm.setParseErrorMessage(I18N.getMessage("common.unparseableDate"));
        voimassaLoppuPvm.addListener(new ValueChangeListener());
        voimassaLoppuPvm.setImmediate(true);

        tila = createTilaTypeSelect();
        tila.setSizeUndefined();
        tila.addListener(new ValueChangeListener());
        tila.setImmediate(true);

        omistaja = createTextField();

        organisaatioOid = SimpleOrganisaatioWidgetFactory.createComponent(KoodistoApplication.getInstance().getUser().getOrganisations());
        organisaatioOid.addListener(new ValueChangeListener());
        organisaatioOid.setImmediate(true);

        paivitysPvm = new DateField();
        paivitysPvm.setDateFormat("dd.MM.yyyy HH:mm");
        paivitysPvm.setReadOnly(true);
        paivitysPvm.setResolution(DateField.RESOLUTION_MIN);
        paivitysPvm.setLocale(LocaleContextHolder.getLocale());
        paivitysPvm.setParseErrorMessage(I18N.getMessage("common.unparseableDate"));
        paivitysPvm.addListener(new ValueChangeListener());
        paivitysPvm.setImmediate(true);

        // FI METADATA
        nimiFi = createTextField();
        kuvausFi = createTextArea();
        kayttoohjeFi = createTextArea();
        kasiteFi = createTextField();
        kohdealueFi = createTextField();
        kohdealueenOsaAlueFi = createTextField();
        sitovuustasoFi = createTextField();
        tarkentaaKoodistoaFi = createTextField();
        huomioitavaKoodistoFi = createTextField();
        koodistonLahdeFi = createTextField();
        toimintaymparistoFi = createTextField();

        // SV METADATA
        nimiSv = createTextField();
        kuvausSv = createTextArea();
        kayttoohjeSv = createTextArea();
        kasiteSv = createTextField();
        kohdealueSv = createTextField();
        kohdealueenOsaAlueSv = createTextField();
        sitovuustasoSv = createTextField();
        tarkentaaKoodistoaSv = createTextField();
        huomioitavaKoodistoSv = createTextField();
        koodistonLahdeSv = createTextField();
        toimintaymparistoSv = createTextField();

        // EN METADATA
        nimiEn = createTextField();
        kuvausEn = createTextArea();
        kayttoohjeEn = createTextArea();
        kasiteEn = createTextField();
        kohdealueEn = createTextField();
        kohdealueenOsaAlueEn = createTextField();
        sitovuustasoEn = createTextField();
        tarkentaaKoodistoaEn = createTextField();
        huomioitavaKoodistoEn = createTextField();
        koodistonLahdeEn = createTextField();
        toimintaymparistoEn = createTextField();
    }

    public TextField getKoodistoUri() {
        return koodistoUri;
    }

    public DateField getPaivitysPvm() {
        return paivitysPvm;
    }

    public Select getTila() {
        return tila;
    }

    public TextField getOmistaja() {
        return omistaja;
    }

    public TextField getVersio() {
        return versio;
    }

    public DateField getVoimassaAlkuPvm() {
        return voimassaAlkuPvm;
    }

    public DateField getVoimassaLoppuPvm() {
        return voimassaLoppuPvm;
    }

    public TextField getNimiFi() {
        return nimiFi;
    }

    public TextArea getKuvausFi() {
        return kuvausFi;
    }

    public TextField getKasiteFi() {
        return kasiteFi;
    }

    public TextArea getKayttoohjeFi() {
        return kayttoohjeFi;
    }

    public TextField getKohdealueFi() {
        return kohdealueFi;
    }

    public TextField getKohdealueenOsaAlueFi() {
        return kohdealueenOsaAlueFi;
    }

    public TextField getToimintaymparistoFi() {
        return toimintaymparistoFi;
    }

    public TextField getNimiSv() {
        return nimiSv;
    }

    public TextArea getKuvausSv() {
        return kuvausSv;
    }

    public TextField getKasiteSv() {
        return kasiteSv;
    }

    public TextArea getKayttoohjeSv() {
        return kayttoohjeSv;
    }

    public TextField getKohdealueSv() {
        return kohdealueSv;
    }

    public TextField getKohdealueenOsaAlueSv() {
        return kohdealueenOsaAlueSv;
    }

    public TextField getToimintaymparistoSv() {
        return toimintaymparistoSv;
    }

    public TextField getNimiEn() {
        return nimiEn;
    }

    public TextArea getKuvausEn() {
        return kuvausEn;
    }

    public TextField getKasiteEn() {
        return kasiteEn;
    }

    public TextArea getKayttoohjeEn() {
        return kayttoohjeEn;
    }

    public TextField getKohdealueEn() {
        return kohdealueEn;
    }

    public TextField getKohdealueenOsaAlueEn() {
        return kohdealueenOsaAlueEn;
    }

    public TextField getToimintaymparistoEn() {
        return toimintaymparistoEn;
    }

    public void setFieldsReadOnly(boolean b) {
        setFieldReadOnly(koodistoUri, b);
        setFieldReadOnly(paivitysPvm, b);
        setFieldReadOnly(tila, b);
        setFieldReadOnly(omistaja, b);
        setFieldReadOnly(organisaatioOid, b);
        organisaatioOid.setSearchButtonVisible(!b);
        setFieldReadOnly(versio, b);
        setFieldReadOnly(voimassaAlkuPvm, b);
        setFieldReadOnly(voimassaLoppuPvm, b);

        setFieldReadOnly(nimiLinkedField, b);
        setFieldReadOnly(kuvausLinkedField, b);
        setFieldReadOnly(kasiteLinkedField, b);
        setFieldReadOnly(kayttoohjeLinkedField, b);
        setFieldReadOnly(kohdealueLinkedField, b);
        setFieldReadOnly(kohdealueenOsaalueLinkedField, b);
        setFieldReadOnly(toimintaymparistoLinkedField, b);
        setFieldReadOnly(koodistonLahdeLinkedField, b);
        setFieldReadOnly(tarkentaaKoodistoaLinkedField, b);
        setFieldReadOnly(huomioitavaKoodistoLinkedField, b);
        setFieldReadOnly(sitovuustasoLinkedField, b);
    }

    @Override
    protected TextField createTextField() {

        TextField textField = super.createTextField();
        textField.addListener(new ValueChangeListener());
        textField.setImmediate(true);

        return textField;

    }

    @Override
    protected TextArea createTextArea() {

        TextArea textArea = super.createTextArea();
        textArea.addListener(new ValueChangeListener());
        textArea.setImmediate(true);

        return textArea;

    }

    private boolean monitorValueChanges = false;

    private class ValueChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent valueChangeEvent) {

            if (monitorValueChanges) {
                KoodistoApplication.getInstance().getPresenter().setDataChanged(true);
            }

        }
    }

    public void startMonitoringValueChanges() {
        monitorValueChanges = true;
    }

}
