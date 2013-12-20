/**
 *
 */
package fi.vm.sade.koodisto.ui.koodi.form;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.LinkedFieldComponent;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.common.util.FieldLengths;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.ui.common.form.AbstractForm;
import fi.vm.sade.koodisto.ui.koodisto.LinkedFieldHelper;
import org.hibernate.validator.constraints.NotBlank;
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
public class KoodiFormLayout extends AbstractForm {

    private static final int VERSIO_FIELD_WIDTH = 4;

    @PropertyId("koodi.koodiUri")
    private TextField koodiUri;

    @NotNull(message = "{koodiEditForm.validate.koodiarvo}")
    @NotBlank(message = "{koodiEditForm.validate.koodiarvo}")
    @PropertyId("koodi.koodiArvo")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.koodiArvo}")
    private TextField koodiarvo;

    @NotNull(message = "{koodiEditForm.validate.tila}")
    @PropertyId("koodi.tila")
    private Select tila;

    @PropertyId("paivitysPvm")
    private DateField paivitysPvm;

    @Min(value = 1, message = "{koodiEditForm.validate.versio}")
    @PropertyId("koodi.versio")
    private TextField versio;

    @NotNull(message = "{koodiEditForm.validate.voimassaAlkuPvm}")
    @PropertyId("voimassaAlkuPvm")
    private DateField voimassaAlkuPvm;

    @PropertyId("voimassaLoppuPvm")
    private DateField voimassaLoppuPvm;

    @PropertyId("fiMetadata.nimi")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.nimiFi}")
    private TextField nimiFi;

    @PropertyId("fiMetadata.lyhytNimi")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.lyhytNimiFi}")
    private TextField lyhytnimiFi;

    @PropertyId("fiMetadata.kuvaus")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.kuvausFi}")
    private TextArea kuvausFi;

    @PropertyId("fiMetadata.kasite")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.kasiteFI}")
    private TextField kasiteFi;

    @PropertyId("fiMetadata.huomioitavaKoodi")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.huomioitavaKoodiFi}")
    private TextField huomioitavaKoodiFi;

    @PropertyId("fiMetadata.eiSisallaMerkitysta")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.eiSisallaMerkitystaFi}")
    private TextArea eiSisallaMerkitystaFi;

    @PropertyId("fiMetadata.sisaltaaMerkityksen")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.sisaltaaMerkityksenFi}")
    private TextArea sisaltaaMerkityksenFi;

    @PropertyId("fiMetadata.sisaltaaKoodiston")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.sisaltaaKoodistonFi}")
    private TextArea sisaltaaKoodistonFi;

    @PropertyId("fiMetadata.kayttoohje")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.kayttoohjeFi}")
    private TextArea kayttoohjeFi;

    @PropertyId("svMetadata.nimi")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.nimiSv}")
    private TextField nimiSv;

    @PropertyId("svMetadata.lyhytNimi")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.lyhytNimiSv}")
    private TextField lyhytnimiSv;

    @PropertyId("svMetadata.kuvaus")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.kuvausSv}")
    private TextArea kuvausSv;

    @PropertyId("svMetadata.kasite")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.kasiteSv}")
    private TextField kasiteSv;

    @PropertyId("svMetadata.huomioitavaKoodi")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.huomioitavaKoodiSv}")
    private TextField huomioitavaKoodiSv;

    @PropertyId("svMetadata.eiSisallaMerkitysta")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.eiSisallaMerkitystaSv}")
    private TextArea eiSisallaMerkitystaSv;

    @PropertyId("svMetadata.sisaltaaMerkityksen")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.sisaltaaMerkityksenSv}")
    private TextArea sisaltaaMerkityksenSv;

    @PropertyId("svMetadata.sisaltaaKoodiston")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.sisaltaaKoodistonSv}")
    private TextArea sisaltaaKoodistonSv;

    @PropertyId("svMetadata.kayttoohje")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.kayttoohjeSv}")
    private TextArea kayttoohjeSv;

    @PropertyId("enMetadata.nimi")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.nimiEn}")
    private TextField nimiEn;

    @PropertyId("enMetadata.lyhytNimi")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.lyhytNimiEn}")
    private TextField lyhytnimiEn;

    @PropertyId("enMetadata.kuvaus")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.kuvausEn}")
    private TextArea kuvausEn;

    @PropertyId("enMetadata.kasite")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.kasiteEn}")
    private TextField kasiteEn;

    @PropertyId("enMetadata.huomioitavaKoodi")
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH, message = "{koodiEditForm.validate.length.huomioitavaKoodiEn}")
    private TextField huomioitavaKoodiEn;

    @PropertyId("enMetadata.eiSisallaMerkitysta")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.eiSisallaMerkitystaEn}")
    private TextArea eiSisallaMerkitystaEn;

    @PropertyId("enMetadata.sisaltaaMerkityksen")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.sisaltaaMerkityksenEn}")
    private TextArea sisaltaaMerkityksenEn;

    @PropertyId("enMetadata.sisaltaaKoodiston")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.sisaltaaKoodistonEn}")
    private TextArea sisaltaaKoodistonEn;

    @PropertyId("enMetadata.kayttoohje")
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH, message = "{koodiEditForm.validate.length.kayttoohjeEn}")
    private TextArea kayttoohjeEn;

    private LinkedFieldComponent nimiLinkedField;
    private LinkedFieldComponent lyhytNimiLinkedField;
    private LinkedFieldComponent kuvausLinkedField;
    private LinkedFieldComponent kayttoohjeLinkedField;
    private LinkedFieldComponent kasiteLinkedField;
    private LinkedFieldComponent huomioitavaKoodiLinkedField;
    private LinkedFieldComponent sisaltaaMerkityksenLinkedField;
    private LinkedFieldComponent eiSisallaMerkitystaLinkedField;
    private LinkedFieldComponent sisaltaaKoodistonLinkedField;

    private LinkedFieldHelper linkedFieldHelper = new LinkedFieldHelper();

    public KoodiFormLayout(boolean isNew, TilaType koodiTila, String baseUri) {
        super(isNew);
        initializeFields(koodiTila);

        GridLayout fieldLayout = createFieldLayout();
        addComponent(fieldLayout);

        if (!isNew) {
            addStateSelect(fieldLayout, I18N.getMessage("koodiEditForm.koodi.state"), tila);
        }

        nimiLinkedField = linkedFieldHelper.createLinkedField(nimiFi, nimiSv, nimiEn);
        addLabelAndContent(fieldLayout, new Label(I18N.getMessage("koodiEditForm.koodi.name")), nimiLinkedField);

        if (!isNew) {
            addUriLayout(fieldLayout, baseUri, koodiUri, I18N.getMessage("koodiEditForm.koodi.uri"));
            addVersio(fieldLayout, versio, I18N.getMessage("koodiEditForm.koodi.versio"));
        }

        addLabelAndContent(fieldLayout, new Label(I18N.getMessage("koodiEditForm.koodi.codeValue")), koodiarvo);

        addValidityLayout(fieldLayout, voimassaAlkuPvm, voimassaLoppuPvm, I18N.getMessage("koodistoEditForm.koodisto.validity"));

        lyhytNimiLinkedField = linkedFieldHelper.createLinkedField(lyhytnimiFi, lyhytnimiSv, lyhytnimiEn);
        addLabelAndContent(fieldLayout, xhtmlLabel(I18N.getMessage("koodiEditForm.koodi.shortName")), lyhytNimiLinkedField);

        kuvausLinkedField = linkedFieldHelper.createLinkedField(kuvausFi, kuvausSv, kuvausEn);
        addLabelAndContent(fieldLayout, new Label(I18N.getMessage("koodiEditForm.koodi.description")), kuvausLinkedField);

        final Button hideShowExtendedMetadata = new Button(I18N.getMessage("koodiEditForm.koodi.showExtendedMetadata"));
        addComponent(hideShowExtendedMetadata);
        setComponentAlignment(hideShowExtendedMetadata, Alignment.MIDDLE_LEFT);
        hideShowExtendedMetadata.setStyleName(BaseTheme.BUTTON_LINK);

        final GridLayout extendedMetaLayout = createExtendedMetadataLayout();
        addComponent(extendedMetaLayout);

        hideShowExtendedMetadata.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (extendedMetaLayout.isVisible()) {
                    extendedMetaLayout.setVisible(false);
                    hideShowExtendedMetadata.setCaption(I18N.getMessage("koodiEditForm.koodi.showExtendedMetadata"));
                } else {
                    extendedMetaLayout.setVisible(true);
                    hideShowExtendedMetadata.setCaption(I18N.getMessage("koodiEditForm.koodi.hideExtendedMetadata"));
                }
            }
        });

        kayttoohjeLinkedField = linkedFieldHelper.createLinkedField(kayttoohjeFi, kayttoohjeSv, kayttoohjeEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodiEditForm.koodi.instructions")), kayttoohjeLinkedField);

        kasiteLinkedField = linkedFieldHelper.createLinkedField(kasiteFi, kasiteSv, kasiteEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodiEditForm.koodi.concept")), kasiteLinkedField);

        huomioitavaKoodiLinkedField = linkedFieldHelper.createLinkedField(huomioitavaKoodiFi, huomioitavaKoodiSv, huomioitavaKoodiEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodiEditForm.koodi.koodiToTakeNoticeOf")), huomioitavaKoodiLinkedField);

        sisaltaaMerkityksenLinkedField = linkedFieldHelper.createLinkedField(sisaltaaMerkityksenFi, sisaltaaMerkityksenSv, sisaltaaMerkityksenEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodiEditForm.koodi.containsSignificance")), sisaltaaMerkityksenLinkedField);

        eiSisallaMerkitystaLinkedField = linkedFieldHelper.createLinkedField(eiSisallaMerkitystaFi, eiSisallaMerkitystaSv, eiSisallaMerkitystaEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodiEditForm.koodi.doesNotContainSignificance")), eiSisallaMerkitystaLinkedField);

        sisaltaaKoodistonLinkedField = linkedFieldHelper.createLinkedField(sisaltaaKoodistonFi, sisaltaaKoodistonSv, sisaltaaKoodistonEn);
        addLabelAndContent(extendedMetaLayout, new Label(I18N.getMessage("koodiEditForm.koodi.containsKoodisto")), sisaltaaKoodistonLinkedField);

        addComponent(createLastUpdatedLayout(new Label(I18N.getMessage("koodiEditForm.koodi.previousUpdate")), paivitysPvm));

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    private void initializeFields(TilaType koodiTila) {
        koodiUri = createTextField();
        koodiUri.setReadOnly(!isNew);
        koodiUri.setVisible(!isNew);
        versio = createTextField();
        versio.setWidth(VERSIO_FIELD_WIDTH, Sizeable.UNITS_EM);
        versio.setReadOnly(true);
        versio.setEnabled(false);
        versio.setVisible(!isNew);
        koodiarvo = createTextField();
        voimassaAlkuPvm = new DateField();
        voimassaAlkuPvm.setResolution(DateField.RESOLUTION_DAY);
        voimassaAlkuPvm.setDateFormat("dd.MM.yyyy");
        voimassaAlkuPvm.setLocale(LocaleContextHolder.getLocale());
        voimassaAlkuPvm.setParseErrorMessage(I18N.getMessage("common.unparseableDate"));

        voimassaLoppuPvm = new DateField();
        voimassaLoppuPvm.setResolution(DateField.RESOLUTION_DAY);
        voimassaLoppuPvm.setDateFormat("dd.MM.yyyy");
        voimassaLoppuPvm.setLocale(LocaleContextHolder.getLocale());
        voimassaLoppuPvm.setParseErrorMessage(I18N.getMessage("common.unparseableDate"));

        tila = createTilaTypeSelect();
        if (koodiTila != null && !TilaType.HYVAKSYTTY.equals(koodiTila)) {
            tila.removeItem(TilaType.HYVAKSYTTY);
        }

        tila.setSizeUndefined();

        paivitysPvm = new DateField();
        paivitysPvm.setResolution(DateField.RESOLUTION_MIN);
        paivitysPvm.setDateFormat("dd.MM.yyyy HH:mm");
        paivitysPvm.setEnabled(false);
        paivitysPvm.setLocale(LocaleContextHolder.getLocale());
        paivitysPvm.setParseErrorMessage(I18N.getMessage("common.unparseableDate"));

        nimiFi = createTextField();
        lyhytnimiFi = createTextField();
        kuvausFi = createTextArea();
        kasiteFi = createTextField();
        huomioitavaKoodiFi = createTextField();
        eiSisallaMerkitystaFi = createTextArea();
        sisaltaaMerkityksenFi = createTextArea();
        sisaltaaKoodistonFi = createTextArea();
        kayttoohjeFi = createTextArea();

        nimiSv = createTextField();
        lyhytnimiSv = createTextField();
        kuvausSv = createTextArea();
        kasiteSv = createTextField();
        huomioitavaKoodiSv = createTextField();
        eiSisallaMerkitystaSv = createTextArea();
        sisaltaaMerkityksenSv = createTextArea();

        sisaltaaKoodistonSv = createTextArea();
        kayttoohjeSv = createTextArea();

        nimiEn = createTextField();
        lyhytnimiEn = createTextField();
        kuvausEn = createTextArea();
        kasiteEn = createTextField();
        huomioitavaKoodiEn = createTextField();
        eiSisallaMerkitystaEn = createTextArea();
        sisaltaaMerkityksenEn = createTextArea();
        sisaltaaKoodistonEn = createTextArea();
        kayttoohjeEn = createTextArea();
    }

    public TextField getKoodiUri() {
        return koodiUri;
    }

    public TextField getKoodiarvo() {
        return koodiarvo;
    }

    public Select getTila() {
        return tila;
    }

    public DateField getPaivitysPvm() {
        return paivitysPvm;
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

    public TextField getLyhytnimiFi() {
        return lyhytnimiFi;
    }

    public TextArea getKuvausFi() {
        return kuvausFi;
    }

    public TextField getKasiteFi() {
        return kasiteFi;
    }

    public TextArea getEiSisallaMerkitystaFi() {
        return eiSisallaMerkitystaFi;
    }

    public TextArea getSisaltaaMerkityksenFi() {
        return sisaltaaMerkityksenFi;
    }

    public TextArea getSisaltaaKoodistonFi() {
        return sisaltaaKoodistonFi;
    }

    public TextArea getKayttoohjeFi() {
        return kayttoohjeFi;
    }

    public TextField getNimiSv() {
        return nimiSv;
    }

    public TextField getLyhytnimiSv() {
        return lyhytnimiSv;
    }

    public TextArea getKuvausSv() {
        return kuvausSv;
    }

    public TextField getKasiteSv() {
        return kasiteSv;
    }

    public TextArea getEiSisallaMerkitystaSv() {
        return eiSisallaMerkitystaSv;
    }

    public TextArea getSisaltaaMerkityksenSv() {
        return sisaltaaMerkityksenSv;
    }

    public TextArea getSisaltaaKoodistonSv() {
        return sisaltaaKoodistonSv;
    }

    public TextArea getKayttoohjeSv() {
        return kayttoohjeSv;
    }

    public TextField getNimiEn() {
        return nimiEn;
    }

    public TextField getLyhytnimiEn() {
        return lyhytnimiEn;
    }

    public TextArea getKuvausEn() {
        return kuvausEn;
    }

    public TextField getKasiteEn() {
        return kasiteEn;
    }

    public TextArea getEiSisallaMerkitystaEn() {
        return eiSisallaMerkitystaEn;
    }

    public TextArea getSisaltaaMerkityksenEn() {
        return sisaltaaMerkityksenEn;
    }

    public TextArea getSisaltaaKoodistonEn() {
        return sisaltaaKoodistonEn;
    }

    public TextArea getKayttoohjeEn() {
        return kayttoohjeEn;
    }

    public void setFieldsReadOnly(boolean b) {
        setFieldReadOnly(koodiUri, b);
        setFieldReadOnly(koodiarvo, b);
        setFieldReadOnly(koodiarvo, b);
        setFieldReadOnly(tila, b);
        setFieldReadOnly(paivitysPvm, b);
        setFieldReadOnly(versio, b);
        setFieldReadOnly(voimassaAlkuPvm, b);
        setFieldReadOnly(voimassaLoppuPvm, b);

        setFieldReadOnly(nimiLinkedField, b);
        setFieldReadOnly(lyhytNimiLinkedField, b);
        setFieldReadOnly(kuvausLinkedField, b);
        setFieldReadOnly(kasiteLinkedField, b);
        setFieldReadOnly(huomioitavaKoodiLinkedField, b);
        setFieldReadOnly(eiSisallaMerkitystaLinkedField, b);
        setFieldReadOnly(sisaltaaMerkityksenLinkedField, b);
        setFieldReadOnly(sisaltaaKoodistonLinkedField, b);
        setFieldReadOnly(kayttoohjeLinkedField, b);
    }
}
