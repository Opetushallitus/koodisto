package fi.vm.sade.koodisto.service.business.marshaller;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;

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

    public abstract Resource marshal(List<KoodiType> koodis, String encoding) throws IOException;

    public abstract List<KoodiType> unmarshal(Resource resource, String encoding) throws IOException;
}
