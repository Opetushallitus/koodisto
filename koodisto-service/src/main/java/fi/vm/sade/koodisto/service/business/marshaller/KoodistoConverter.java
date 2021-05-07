package fi.vm.sade.koodisto.service.business.marshaller;

import fi.jhs_suositukset.skeemat.oph._2012._05._03.KoodiListaus;
import fi.vm.sade.koodisto.service.types.common.KoodiCollectionType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import org.apache.commons.lang.StringUtils;

import javax.activation.DataHandler;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 8.51
 */
public abstract class KoodistoConverter {

    protected static final Charset UTF8ENCODING = StandardCharsets.UTF_8;
    protected static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    protected Charset getCharset(String encoding) {
        Charset charset;

        if (StringUtils.isNotBlank(encoding) && Charset.isSupported(encoding)) {
            charset = Charset.forName(encoding);
        } else {
            charset = DEFAULT_ENCODING;
        }

        return charset;
    }

    public abstract DataHandler marshal(KoodiListaus koodis, String encoding) throws IOException;

    public abstract KoodiListaus unmarshal(DataHandler handler, String encoding) throws IOException;
}
