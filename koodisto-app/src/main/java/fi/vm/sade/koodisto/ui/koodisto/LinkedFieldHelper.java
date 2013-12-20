package fi.vm.sade.koodisto.ui.koodisto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Label;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.LinkedFieldComponent;

public class LinkedFieldHelper implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7149084316086007256L;

    public LinkedFieldComponent createLinkedField(String linkingText, AbstractField primaryField, Label primaryFieldLabel, Map<AbstractField, Label> otherFields) {
        LinkedFieldComponent linkedComponent = new LinkedFieldComponent(linkingText, primaryField, primaryFieldLabel, otherFields);
        return linkedComponent;
    }

    public LinkedFieldComponent createLinkedField(AbstractField fiField, AbstractField svField, AbstractField enField) {
        return createLinkedField(I18N.getMessage("linkedFields.sameInEveryLanguage"), fiField, new Label(I18N.getMessage("language.finnish")),
                createLinkedFieldMap(svField, enField));
    }

    private Map<AbstractField, Label> createLinkedFieldMap(AbstractField svField, AbstractField enField) {
        Map<AbstractField, Label> map = new LinkedHashMap<AbstractField, Label>();
        map.put(svField, new Label(I18N.getMessage("language.swedish")));
        map.put(enField, new Label(I18N.getMessage("language.english")));

        return map;
    }

}