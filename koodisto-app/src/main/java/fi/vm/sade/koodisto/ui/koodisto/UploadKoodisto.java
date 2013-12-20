package fi.vm.sade.koodisto.ui.koodisto;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Select;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author kkammone
 * 
 */
@SuppressWarnings("serial")
public class UploadKoodisto implements Upload.SucceededListener, Upload.Receiver {
    private Window window;

    private ByteArrayOutputStream uploadedFile;

    private Select encodingSelect;
    private Select formatSelect;
    private Upload upload;

    private String koodistoUri;
    private Window dialog;

    private final KoodistoPresenter presenter;

    public UploadKoodisto(KoodistoPresenter presenter, Window window, String koodistoUri) {
        this.presenter = presenter;
        this.window = window;
        this.koodistoUri = koodistoUri;
        initialize();
    }

    public void initialize() {
        dialog = new Window(I18N.getMessage("uploadKoodisto.header"));
        dialog.setModal(true);
        dialog.center();
        dialog.setWidth("50%");
        window.addWindow(dialog);
        upload = new Upload(I18N.getMessage("uploadKoodisto.sendFile"), this);
        upload.setButtonCaption(I18N.getMessage("uploadKoodisto.button.send.caption"));
        upload.addListener((Upload.SucceededListener) this);
        upload.setEnabled(false);

        encodingSelect = createEncodingSelect();
        formatSelect = createFormatSelect(encodingSelect);

        dialog.addComponent(formatSelect);
        dialog.addComponent(encodingSelect);
        dialog.addComponent(upload);
    }

    private Select createEncodingSelect() {
        final Select encodingSelect = new Select(I18N.getMessage("uploadKoodisto.encoding"));
        for (Encoding encoding : Encoding.values()) {
            encodingSelect.addItem(encoding);
        }
        encodingSelect.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (formatSelect.getValue() != null && encodingSelect.getValue() != null) {
                    upload.setEnabled(true);
                } else {
                    upload.setEnabled(false);
                }
            }
        });
        encodingSelect.setImmediate(true);
        encodingSelect.setVisible(false);
        return encodingSelect;
    }

    private Select createFormatSelect(final Select encodingSelect) {
        final Select select = new Select(I18N.getMessage("uploadKoodisto.format"));
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
                    upload.setEnabled(false);
                } else {
                    upload.setEnabled(true);
                }
            }
        });
        return select;
    }

    public OutputStream receiveUpload(String filename, String mimeType) {
        uploadedFile = new ByteArrayOutputStream();
        return uploadedFile;
    }

    public void uploadSucceeded(SucceededEvent event) {
        InputStream csvData = new ByteArrayInputStream(uploadedFile.toByteArray());

        presenter.upload(dialog, csvData, koodistoUri, (Format) this.formatSelect.getValue(), (Encoding) this.encodingSelect.getValue());

    }

}
