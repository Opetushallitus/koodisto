package fi.vm.sade.koodisto.ui.koodisto;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.koodisto.ui.service.KoodiUiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.InputStream;

@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class DownloadKoodisto {

    private Window parentWindow;
    private Select encodingSelect;
    private Select formatSelect;
    private Button downloadKoodistoButton;
    private String koodistoUri;
    private Integer koodistoVersio;
    private Window dialog;

    private final KoodistoPresenter presenter;

    @Autowired
    private KoodiUiService koodiUiService;

    public DownloadKoodisto(KoodistoPresenter presenter, Window parentWindow, String koodistoUri, Integer koodistoVersio) {
        this.presenter = presenter;
        this.parentWindow = parentWindow;
        this.koodistoUri = koodistoUri;
        this.koodistoVersio = koodistoVersio;
        initialize();
    }

    private void initialize() {
        dialog = new Window(I18N.getMessage("downloadKoodisto.header"));
        dialog.setModal(true);
        dialog.center();
        dialog.setWidth("50%");
        parentWindow.addWindow(dialog);

        downloadKoodistoButton = createDownloadButton();
        encodingSelect = createEncodingSelect();
        formatSelect = createFormatSelect(encodingSelect);

        dialog.addComponent(formatSelect);
        dialog.addComponent(encodingSelect);
        dialog.addComponent(downloadKoodistoButton);

    }

    private Button createDownloadButton() {
        Button downloadKoodistoCSVButton = new Button(I18N.getMessage("downloadKoodisto.downloadButton"));
        downloadKoodistoCSVButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                String filetype = "";
                if (formatSelect.getValue() != null && formatSelect.getValue() == Format.CSV) {
                    filetype = ".csv";
                } else if (formatSelect.getValue() != null && formatSelect.getValue() == Format.JHS_XML) {
                    filetype = ".xml";
                }

                parentWindow.removeWindow(dialog);
                StreamSource source = new CSVStreamSource();
                if (source.getStream() != null) {
                    StreamResource resource = new StreamResource(source, koodistoUri + filetype, parentWindow.getApplication());
                    resource.setMIMEType("application/octet-stream");

                    parentWindow.showNotification(I18N.getMessage("downloadKoodisto.receiving.success"), Notification.TYPE_HUMANIZED_MESSAGE);

                    parentWindow.getApplication().getMainWindow().open(resource, "_top");
                } else {
                    parentWindow.showNotification(I18N.getMessage("downloadKoodisto.receiving.failed"), Notification.TYPE_ERROR_MESSAGE);
                }

            }
        });
        downloadKoodistoCSVButton.setEnabled(false);
        return downloadKoodistoCSVButton;
    }

    private Select createEncodingSelect() {
        final Select encodingSelect = new Select(I18N.getMessage("downloadKoodisto.encoding"));
        for (Encoding encoding : Encoding.values()) {
            encodingSelect.addItem(encoding);
        }
        encodingSelect.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (formatSelect.getValue() != null && encodingSelect.getValue() != null) {
                    downloadKoodistoButton.setEnabled(true);
                } else {
                    downloadKoodistoButton.setEnabled(false);
                }
            }
        });
        encodingSelect.setImmediate(true);
        encodingSelect.setVisible(false);
        return encodingSelect;
    }

    private Select createFormatSelect(final Select encodingSelect) {
        final Select select = new Select(I18N.getMessage("downloadKoodisto.format"));
        for (Format format : Format.values()) {
            select.addItem(format);
        }
        select.setImmediate(true);
        select.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (select.getValue() != null && select.getValue() == Format.CSV) {
                    encodingSelect.setVisible(true);
                } else {
                    encodingSelect.setVisible(false);
                }
                if (select.getValue() == null || (select.getValue() == Format.CSV && encodingSelect.getValue() == null)) {
                    downloadKoodistoButton.setEnabled(false);
                } else {
                    downloadKoodistoButton.setEnabled(true);
                }
            }
        });
        return select;
    }

    private class CSVStreamSource implements StreamSource {

        @Override
        public InputStream getStream() {
            Format format = (Format) formatSelect.getValue();
            String encodingStr = "";
            Encoding encoding = (Encoding) encodingSelect.getValue();
            if (encoding != null) {
                encodingStr = encoding.getStringValue();
            }
            return presenter.download(dialog, koodistoUri, koodistoVersio, format, encodingStr);
        }
    }

}
