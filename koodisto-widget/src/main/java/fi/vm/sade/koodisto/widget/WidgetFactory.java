/**
 *
 */
package fi.vm.sade.koodisto.widget;

/**
 * @author tommiha
 */
public class WidgetFactory {

    @Deprecated
    public static KoodistoComponent create(String koodistoUri) {
        WidgetFactory instance = getInstance();
        return instance.createComponent(koodistoUri);
    }

    @Deprecated
    public static KoodistoComponent create(String koodistoUri, boolean onlyValidKoodis) {
        WidgetFactory instance = getInstance();
        return instance.createComponent(koodistoUri, onlyValidKoodis);
    }    
    
    @Deprecated
    public static WidgetFactory getInstance(){
        return new WidgetFactory();
    }

    /**
     * Creates a new KoodistoComponent for given koodisto URI.
     *
     * @param koodistoUri
     * @return
     */
    public KoodistoComponent createComponent(String koodistoUri) {
        return new KoodistoComponent(koodistoUri);
    }
    
    public KoodistoComponent createComponent(String koodistoUri, boolean onlyValidKoodis) {
        return new KoodistoComponent(koodistoUri, onlyValidKoodis);
    }    

}
