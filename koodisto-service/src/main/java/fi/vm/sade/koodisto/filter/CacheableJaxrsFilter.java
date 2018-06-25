package fi.vm.sade.koodisto.filter;

import fi.vm.sade.generic.rest.Cacheable;
import org.springframework.stereotype.Component;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * Filter that applies cache headers to Jersey REST responses defined by @Cacheable -annotations.
 * NOTE: this probably does nothing since nginx automatically adds no-cache Cache-Control headers.
 * @see Cacheable
 */
@Component
public class CacheableJaxrsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {

        this.getFirstCacheableAnnotation(responseContext)
                .ifPresent(cacheableAnnotation ->
                        responseContext.getHeaders().putSingle("Cache-Control", "public, max-age=" + cacheableAnnotation.maxAgeSeconds()));
    }

    private Optional<Cacheable> getFirstCacheableAnnotation(ContainerResponseContext responseContext) {
        for (Annotation annotation : responseContext.getEntityAnnotations()) {
            if (Cacheable.class.isAssignableFrom(annotation.getClass())) {
                return Optional.of((Cacheable) annotation);
            }
        }
        return Optional.empty();
    }
}
