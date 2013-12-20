package fi.vm.sade.koodisto.ui.common.form;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.common.util.FieldLengths;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.ui.koodisto.LinkedFieldHelper;
import fi.vm.sade.vaadin.Oph;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public abstract class AbstractForm extends VerticalLayout {

    private static final String FULL = "100%";
    protected final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    protected final LinkedFieldHelper linkedFieldHelper = new LinkedFieldHelper();
    protected final boolean isNew;
    protected boolean userChangedUri = false;

    public AbstractForm(boolean isNew) {
        this.isNew = isNew;
        setSpacing(true);
        setWidth(FULL);
    }

    protected void addLabelAndContent(GridLayout gridLayout, Component label, Component content,
            boolean setContentWidthTo100) {
        addLabelAndContent(gridLayout, label, content, setContentWidthTo100, Alignment.TOP_RIGHT);
    }

    protected void addLabelAndContent(GridLayout gridLayout, Component label, Component content,
            Alignment labelAlignment) {
        addLabelAndContent(gridLayout, label, content, true, labelAlignment);
    }

    protected void addLabelAndContent(GridLayout gridLayout, Component label, Component content,
            boolean setContentWidthTo100, Alignment labelAlignment) {
        int row = gridLayout.getRows();
        gridLayout.insertRow(row);

        label.setSizeUndefined();
        if (setContentWidthTo100) {
            content.setWidth(FULL);
        }
        gridLayout.addComponent(label, 0, row);
        gridLayout.setComponentAlignment(label, labelAlignment);
        gridLayout.addComponent(content, 1, row);
        gridLayout.setComponentAlignment(content, Alignment.TOP_LEFT);
    }

    protected void addLabelAndContent(GridLayout gridLayout, Component label, Component content) {
        addLabelAndContent(gridLayout, label, content, true);
    }

    protected static Label xhtmlLabel(String caption) {
        Label label = new Label(caption);
        label.setStyleName(Oph.TEXT_ALIGN_RIGHT);
        label.setContentMode(Label.CONTENT_XHTML);

        return label;
    }

    protected void setFieldReadOnly(AbstractComponent f, boolean b) {
        f.setReadOnly(f.isReadOnly() ? true : b);
    }

    protected Select createTilaTypeSelect() {
        Select select = new Select();
        select.setNullSelectionAllowed(false);
        select.addContainerProperty("caption", String.class, "");

        for (TilaType tila : TilaType.values()) {
            Item item = select.addItem(tila);
            item.getItemProperty("caption").setValue(I18N.getMessage("componentUtils.state." + tila.name()));
        }

        select.setItemCaptionPropertyId("caption");
        return select;
    }

    protected TextField createTextField() {
        final TextField textField = new TextField();
        textField.setNullRepresentation("");
        textField.setWidth(FULL);
        textField.setMaxLength(FieldLengths.DEFAULT_FIELD_LENGTH);
        return textField;
    }

    protected TextArea createTextArea() {
        final TextArea textArea = new TextArea();
        textArea.setNullRepresentation("");
        textArea.setStyleName(Oph.RESIZE_DISABLED);
        textArea.setMaxLength(FieldLengths.LONG_FIELD_LENGTH);
        return textArea;
    }

    protected GridLayout createExtendedMetadataLayout() {
        final GridLayout extendedMetaLayout = new GridLayout(2, 1);
        extendedMetaLayout.setWidth(FULL);
        extendedMetaLayout.setColumnExpandRatio(1, 1.0f);
        extendedMetaLayout.setSpacing(true);
        extendedMetaLayout.setVisible(false);
        return extendedMetaLayout;
    }

    protected GridLayout createFieldLayout() {
        GridLayout fieldLayout = new GridLayout(2, 1);
        fieldLayout.setWidth(FULL);
        fieldLayout.setColumnExpandRatio(1, 1.0f);
        fieldLayout.setSpacing(true);
        return fieldLayout;
    }

    protected void addValidityLayout(GridLayout fieldLayout, DateField voimassaAlkuPvm, DateField voimassaLoppuPvm,
            String caption) {
        GridLayout voimassaoloLayout = new GridLayout(3, 1);
        voimassaoloLayout.setSpacing(true);
        voimassaoloLayout.setColumnExpandRatio(0, 1.0f);
        voimassaoloLayout.setColumnExpandRatio(2, 1.0f);
        voimassaoloLayout.addComponent(voimassaAlkuPvm, 0, 0);
        voimassaAlkuPvm.setStyleName(Oph.CALENDAR_MINIFIED);

        Label separator = new Label("-");
        separator.setSizeUndefined();

        voimassaoloLayout.addComponent(separator, 1, 0);
        voimassaoloLayout.setComponentAlignment(separator, Alignment.MIDDLE_CENTER);
        voimassaoloLayout.addComponent(voimassaLoppuPvm, 2, 0);
        voimassaLoppuPvm.setStyleName(Oph.CALENDAR_MINIFIED);

        addLabelAndContent(fieldLayout, new Label(caption), voimassaoloLayout, Alignment.MIDDLE_RIGHT);
    }

    protected HorizontalLayout createLastUpdatedLayout(Label paivitysPvmCaption, final DateField paivitysPvm) {
        final Label paivitysPvmLabel = new Label();
        paivitysPvm.setVisible(false);
        paivitysPvm.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue() != null) {
                    paivitysPvmLabel.setValue(sdf.format((Date) event.getProperty().getValue()));
                    paivitysPvm.setVisible(true);
                }
            }
        });

        HorizontalLayout pvmLayout = new HorizontalLayout();
        pvmLayout.setSpacing(true);
        pvmLayout.addComponent(paivitysPvmCaption);
        pvmLayout.addComponent(paivitysPvmLabel);
        return pvmLayout;
    }

    protected void addUriLayout(GridLayout fieldLayout, String baseUri, TextField uri, String uriCaption) {
        GridLayout layout = new GridLayout(1, 2);
        layout.setColumnExpandRatio(1, 1.0f);

        Label baseUriLabel = new Label(addSlashToUriStringIfNeeded(baseUri));
        baseUriLabel.setSizeUndefined();
        layout.addComponent(baseUriLabel, 0, 0);
        layout.setComponentAlignment(baseUriLabel, Alignment.MIDDLE_LEFT);
        layout.addComponent(uri, 0, 1);
        addLabelAndContent(fieldLayout, new Label(uriCaption), layout, Alignment.MIDDLE_LEFT);
    }

    protected void addVersio(GridLayout fieldLayout, TextField versio, String versioCaption) {
        addLabelAndContent(fieldLayout, new Label(versioCaption), versio, false, Alignment.MIDDLE_RIGHT);
    }

    protected void addStateSelect(GridLayout fieldLayout, String caption, Select tila) {
        addLabelAndContent(fieldLayout, new Label(caption), tila, false, Alignment.MIDDLE_RIGHT);
    }

    private String addSlashToUriStringIfNeeded(String uri) {
        if (!uri.endsWith("/")) {
            return uri + "/";
        }

        return uri;
    }
}
