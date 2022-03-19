
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoodistoRyhmaListType implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String koodistoRyhmaUri;
    private List<KoodistoRyhmaMetadataType> koodistoRyhmaMetadatas;
    private List<KoodistoListType> koodistos;

    public String getKoodistoRyhmaUri() {
        return koodistoRyhmaUri;
    }

    public void setKoodistoRyhmaUri(String value) {
        this.koodistoRyhmaUri = value;
    }

    public List<KoodistoRyhmaMetadataType> getKoodistoRyhmaMetadatas() {
        if (koodistoRyhmaMetadatas == null) {
            koodistoRyhmaMetadatas = new ArrayList<KoodistoRyhmaMetadataType>();
        }
        return this.koodistoRyhmaMetadatas;
    }

    public List<KoodistoListType> getKoodistos() {
        if (koodistos == null) {
            koodistos = new ArrayList<KoodistoListType>();
        }
        return this.koodistos;
    }
}
