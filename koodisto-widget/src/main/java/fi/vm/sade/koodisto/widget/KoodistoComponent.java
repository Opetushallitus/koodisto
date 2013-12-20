/**
 * 
 */
package fi.vm.sade.koodisto.widget;

import com.vaadin.data.Container.Sortable;
import com.vaadin.data.util.DefaultItemSorter;
import com.vaadin.data.util.ItemSorter;
import com.vaadin.terminal.SystemError;
import fi.vm.sade.generic.ui.component.WrapperComponent;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.util.CachingKoodistoClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract component base class for koodisto widgets.
 * 
 * @author tommiha
 * 
 */
public class KoodistoComponent extends WrapperComponent<KoodiType> {

	private static final long serialVersionUID = 1L;

	private static final ItemSorter UNSORTER = new ItemSorter() {
		private static final long serialVersionUID = 1L;
		@Override
		public void setSortProperties(Sortable container, Object[] propertyId, boolean[] ascending) { }		
		@Override
		public int compare(Object itemId1, Object itemId2) {
			return 0;
		}
	};

    private final CachingKoodistoClient cachingKoodistoClient = new CachingKoodistoClient();

    private String koodistoUri;	
	private Comparator<KoodiType> comparator = null;
	private boolean onlyValidKoodis = false;
	
    public KoodistoComponent(String koodistoUri) {
        super(new DefaultKoodiCaptionFormatter(), new DefaultKoodiFieldValueFormatter());
        this.koodistoUri = koodistoUri;
        captionFormatter = new DefaultKoodiCaptionFormatter();
        fieldValueFormatter = new DefaultKoodiFieldValueFormatter();
    }

    public KoodistoComponent(String koodistoUri, boolean onlyValidKoodis) {
        super(new DefaultKoodiCaptionFormatter(), new DefaultKoodiFieldValueFormatter());
        this.koodistoUri = koodistoUri;
        captionFormatter = new DefaultKoodiCaptionFormatter();
        fieldValueFormatter = new DefaultKoodiFieldValueFormatter();
        this.onlyValidKoodis = onlyValidKoodis;
    }
    
    private KoodistoType getLatestAccepted(String koodistoUri) {
        return cachingKoodistoClient.getKoodistoTypeByUri(koodistoUri);
    }

    private List<KoodiType> getKoodisByKoodisto(String koodistoUri, Integer koodistoVersio) {
        return cachingKoodistoClient.getKoodisForKoodisto(koodistoUri, koodistoVersio);
    }
    
    private List<KoodiType> getKoodisByKoodisto(String koodistoUri, Integer koodistoVersio, boolean onlyValidKoodis) {
        return cachingKoodistoClient.getKoodisForKoodisto(koodistoUri, koodistoVersio, onlyValidKoodis);
    }
    
    protected List<KoodiType> loadOptions() {
        try {
            KoodistoType koodisto = getLatestAccepted(koodistoUri);
            List<KoodiType> ret = getKoodisByKoodisto(koodisto.getKoodistoUri(), koodisto.getVersio(), onlyValidKoodis);
            if (comparator!=null) {
    			Collections.sort(ret, comparator);
    		}
            return ret;
        } catch (Exception e) {
            log.error("Koodisto with URL " + koodistoUri + " was not found.", e);
            field.setComponentError(new SystemError("Koodistoa ei löytynyt."));
            return new ArrayList<KoodiType>();
        }
    }
 
    
	public Comparator<KoodiType> getComparator() {
		return comparator;
	}
	
	public void setComparator(Comparator<KoodiType> comparator) {
		this.comparator = comparator;
		// estää setFieldValues():ssa suoritetun sorttauksen
		container.setItemSorter(comparator == null ? new DefaultItemSorter() : UNSORTER);
	}

}
