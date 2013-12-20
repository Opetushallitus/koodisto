/**
 * 
 */
package demo;

import fi.vm.sade.koodisto.widget.WidgetFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bsb.common.vaadin.embed.support.EmbedVaadin;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;

/**
 * @author tommiha
 * 
 */
public class Demo {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("demo-context.xml");
        EmbedVaadin.forComponent(createDemoComponent()).openBrowser(true).start();
    }

    @SuppressWarnings("serial")
    private static Component createDemoComponent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setSpacing(true);
        final Form form = new Form(formLayout);
        form.setReadThrough(true);

        KoodistoComponent k1 = WidgetFactory.create("http://www.kunnat.fi/kunta");
        Select select = new Select("Perusselect kuntien arvoilla.");
        k1.setCaptionFormatter(new CaptionFormatter<KoodiType>() {

            @Override
            public String formatCaption(KoodiType koodiDTO) {
                return koodiDTO.getKoodiArvo();
            }
        });
        k1.setField(select);
        form.addField("kunta", k1);

        KoodistoComponent k2 = WidgetFactory.create("http://www.kunnat.fi/kunta");
        ListSelect multiSelect = new ListSelect("Monivalintaselect kunnilla ruotsiksi.");
        multiSelect.setMultiSelect(true);
        k2.setField(multiSelect);
        form.addField("kuntaRuotsi", k2);

        KoodistoComponent k3 = WidgetFactory.create("http://www.kunnat.fi/maakunta");
        ComboBox comboBox = new ComboBox("Maakunnat autocompletelistalla (hakusana sisältyy).");
        comboBox.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        k3.setField(comboBox);
        form.addField("maakunta", k3);

        KoodistoComponent k4 = WidgetFactory.create("http://www.kunnat.fi/maakunta");
        OptionGroup options = new OptionGroup("Maakunnat ruotsiksi radiobuttoneilla.");
        k4.setField(options);
        form.addField("maakuntaRuotsi", k4);

        KoodistoComponent k5 = WidgetFactory.create("http://www.avi.fi/aluevirasto");
        OptionGroup checkboxes = new OptionGroup("Aluevirastot checkboxeilla.");
        checkboxes.setMultiSelect(true);
        k5.setField(checkboxes);
        form.addField("avi", k5);

        Button showValues = new Button("Näytä valitut arvot");
        showValues.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                form.commit();
                String value = "[";
                for (Object id : form.getItemPropertyIds()) {
                    value += "\n\t" + id + ": " + form.getItemProperty(id).getValue();
                }
                value += "\n]";
                event.getComponent().getApplication().getMainWindow().showNotification(value);
            }
        });

        layout.addComponent(form);
        layout.addComponent(showValues);

        return layout;
    }
}
