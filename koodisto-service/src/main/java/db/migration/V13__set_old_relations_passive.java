package db.migration;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import fi.vm.sade.koodisto.model.Tila;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class V13__set_old_relations_passive extends BaseJavaMigration {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(V13__set_old_relations_passive.class);
    
    private List<CodesUriVersionDto> codesUriVersions;
    private Map<Long, String> errorMap = new HashMap<>();
    
    public void migrate(Context context) throws Exception {
        JdbcTemplate jdbcTemplate =
                new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));
        LOGGER.warn("Starting migration of setting old relations to passive where applicable");
        handleCodesRelations(jdbcTemplate);
        handleCodeElementsRelations(jdbcTemplate);
        logErrors();
    }

    private void handleCodeElementsRelations(JdbcTemplate jdbcTemplate) {
        List<RelationDto> codeElementRelations = jdbcTemplate.query("SELECT * FROM koodinsuhde ks", new RowMapper<RelationDto>() {

            @Override
            public RelationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new RelationDto(rs.getLong("id"), rs.getLong("ylakoodiversio_id"), rs.getLong("alakoodiversio_id"));
            }
            
        });
        
        final List<UriVersionDto> uriVersions = jdbcTemplate.query("SELECT kv.*, k.koodiuri FROM koodiversio kv, koodi k WHERE kv.koodi_id = k.id",
                new RowMapper<UriVersionDto>() {

                    @Override
                    public UriVersionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new UriVersionDto(rs.getLong("id"), rs.getInt("versio"), rs.getString("koodiuri"), Tila.valueOf(rs.getString("tila")));
                    }
        });
        Collection<RelationDto> relationsToUpdate = Collections2.filter(codeElementRelations, 
                new CodeElementRelationsToPassivePredicate(jdbcTemplate, uriVersions));
        LOGGER.warn("Updating " + relationsToUpdate.size() + "  koodinsuhde rows to passive");
        updateCodeElementRelationsToPassive(jdbcTemplate, relationsToUpdate);
    }


    private void logErrors() {
        LOGGER.error("Script completed with " + errorMap.size() + " errors.");
        for(Long key : errorMap.keySet()) {
            LOGGER.error(errorMap.get(key));
        }
    }

    private void handleCodesRelations(final JdbcTemplate jdbcTemplate) {
        List<RelationDto> codesRelations = jdbcTemplate.query("SELECT * FROM koodistonsuhde ks", new RowMapper<RelationDto>() {

            @Override
            public RelationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new RelationDto(rs.getLong("id"), rs.getLong("ylakoodistoversio_id"), rs.getLong("alakoodistoversio_id"));
            }
            
        });
        
        List<CodesUriVersionDto> uriVersions = getCodesUriVersions(jdbcTemplate);
        
        Collection<RelationDto> relationsToUpdate = Collections2.filter(codesRelations, new RelationsToPassivePredicate<CodesUriVersionDto>(uriVersions));
        LOGGER.warn("Updating " + relationsToUpdate.size() + "  koodistonsuhde rows to passive");
        updateCodesRelationsToPassive(jdbcTemplate, relationsToUpdate);
    }

    private List<CodesUriVersionDto> getCodesUriVersions(final JdbcTemplate jdbcTemplate) {
        if (codesUriVersions == null) {
            codesUriVersions = jdbcTemplate.query("SELECT kv.*, k.koodistouri FROM koodistoversio kv, koodisto k WHERE kv.koodisto_id = k.id",
                    new RowMapper<CodesUriVersionDto>() {

                @Override
                public CodesUriVersionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    List<Long> koodiVersios = jdbcTemplate.query("SELECT kvkv.koodiversio_id FROM koodistoversio_koodiversio kvkv WHERE kvkv.koodistoversio_id = '" + rs.getLong("id") + "'",
                            new RowMapper<Long>() {

                        @Override
                        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getLong("koodiversio_id");
                        }

                    });
                    return new CodesUriVersionDto(rs.getLong("id"), rs.getInt("versio"), rs.getString("koodistouri"), Tila.valueOf(rs.getString("tila")), koodiVersios);
                }

            });
        }
        return codesUriVersions;
    }
    
    private void updateCodeElementRelationsToPassive(JdbcTemplate jdbcTemplate, Collection<RelationDto> relationsToUpdate) {
        for (RelationDto relationDto : relationsToUpdate) {
            jdbcTemplate.update("UPDATE koodinsuhde SET ylakoodistapassiivinen = ?, alakoodistapassiivinen = ? WHERE id = ?", 
                    relationDto.upperPassive, relationDto.lowerPassive, relationDto.id);
        }
        LOGGER.warn(relationsToUpdate.size() + " koodinsuhde rows was succesfully set to passive");
    }
    
    private void updateCodesRelationsToPassive(JdbcTemplate jdbcTemplate, Collection<RelationDto> relationsToUpdate) {
        for (RelationDto relationDto : relationsToUpdate) {
            jdbcTemplate.update("UPDATE koodistonsuhde SET ylakoodistostapassiivinen = ?, alakoodistostapassiivinen = ? WHERE id = ?", 
                    relationDto.upperPassive, relationDto.lowerPassive, relationDto.id);
        }
        LOGGER.warn(relationsToUpdate.size() + " koodistonsuhde rows was succesfully set to passive");
    }

    private static class RelationDto {
        private final Long id;
        private final Long upper, lower;
        private boolean upperPassive = false, lowerPassive = false;
        
        private RelationDto(Long id, Long upper, Long lower) {
            this.id = id;
            this.upper = upper;
            this.lower = lower;
        }
        
    }
    
    private static class UriVersionDto implements Comparable<UriVersionDto>{
        private final Long id;
        private final Integer versio;
        final String uri;
        final Tila tila;
        
        private UriVersionDto(long id, int versio, String uri, Tila tila) {
            this.id = id;
            this.versio = versio;
            this.uri = uri;
            this.tila = tila;
        }

        @Override
        public int compareTo(UriVersionDto arg0) {
            return versio.compareTo(arg0.versio);
        }
    }
    
    private static class CodesUriVersionDto extends UriVersionDto {
        
        private final List<Long> codeElementVersions;
        
        private CodesUriVersionDto(long id, int versio, String uri, Tila tila, List<Long> codeElementVersions) {
            super(id, versio, uri, tila);
            this.codeElementVersions = codeElementVersions;
        }
    }
    
    private class RelationsToPassivePredicate<T extends UriVersionDto> implements Predicate<RelationDto> {
        
        private final List<T> uriVersions;
        
        public RelationsToPassivePredicate(List<T> uriVersions) {
            this.uriVersions = uriVersions;
        }
        
        @Override
        public boolean apply(RelationDto input) {
            makeRelationPassiveIfRequired(input);
            return input.upperPassive || input.lowerPassive;
        }

        private void makeRelationPassiveIfRequired(RelationDto input) {
            input.upperPassive = !isRelationActive(input.upper);
            input.lowerPassive = !isRelationActive(input.lower);
        }

        private boolean isRelationActive(final Long upper) {
            final UriVersionDto uriVersion = Iterables.find(uriVersions, new Predicate<UriVersionDto>() {

                @Override
                public boolean apply(UriVersionDto input) {
                    return upper.equals(input.id);
                }
                
            });
            Collection<T> matchingVersions = Collections2.filter(uriVersions, new Predicate<T>() {

                @Override
                public boolean apply(UriVersionDto input) {
                    return input.uri.equals(uriVersion.uri);
                }
            });
            return isRelationActive(uriVersion, matchingVersions);
        }

        protected boolean isRelationActive(UriVersionDto uriVersion, Collection<T> matchingVersions) {
            UriVersionDto latest = getLatestVersion(uriVersion, matchingVersions);
            return !Tila.PASSIIVINEN.equals(latest.tila) && latest.versio == uriVersion.versio;
        }

        private UriVersionDto getLatestVersion(UriVersionDto uriVersion, Collection<T> matchingVersions) {
            UriVersionDto latest = uriVersion;
            for (UriVersionDto uriVersionDto : matchingVersions) {
                latest = uriVersionDto.versio > latest.versio ? uriVersionDto : latest;
            }
            return latest;
        }
    }
    
    private class CodeElementRelationsToPassivePredicate extends RelationsToPassivePredicate<UriVersionDto> {
        
        private final JdbcTemplate jdbcTemplate;
        
        public CodeElementRelationsToPassivePredicate(JdbcTemplate jdbcTemplate, List<UriVersionDto> uriVersions) {
            super(uriVersions);
            this.jdbcTemplate = jdbcTemplate;
        }
        
        @Override
        protected boolean isRelationActive(UriVersionDto uriVersion, Collection<UriVersionDto> matchingVersions) {
            return super.isRelationActive(uriVersion, matchingVersions) 
                    && isCodeElementVersionInLatestCodes(super.getLatestVersion(uriVersion, matchingVersions));
        }

        private boolean isCodeElementVersionInLatestCodes(UriVersionDto latestVersion) {
            try {
                List<CodesUriVersionDto> codesVersions = getMatchingCodesUriVersions(latestVersion);
                Collections.sort(codesVersions);
                CodesUriVersionDto latestCodes = codesVersions.get(codesVersions.size() - 1);
                return latestCodes.codeElementVersions.contains(latestVersion.id);
            } catch (Exception e) {
                errorMap.put(latestVersion.id, "Error while handling status of latestVersion[id=" + latestVersion.id + ", uri=" +latestVersion.uri +"]. Reason was: " + e.getMessage());
                return false;
            }
        }

        private List<CodesUriVersionDto> getMatchingCodesUriVersions(UriVersionDto latestVersion) {
            List<CodesUriVersionDto> codesVersions = getCodesUriVersions(jdbcTemplate);
            final CodesUriVersionDto firstFound = getFirstFoundMatchingCodesVersio(latestVersion, codesVersions);
            return new ArrayList<CodesUriVersionDto>(Collections2.filter(codesVersions, new Predicate<CodesUriVersionDto>() {

                @Override
                public boolean apply(CodesUriVersionDto input) {
                    return firstFound.uri.equals(input.uri);
                }
                
            }));
        }

        private CodesUriVersionDto getFirstFoundMatchingCodesVersio(UriVersionDto latestVersion, List<CodesUriVersionDto> codesVersions) {
            for (CodesUriVersionDto codesUriVersionDto : codesVersions) {
                if(codesUriVersionDto.codeElementVersions.contains(latestVersion.id)) {
                    return codesUriVersionDto;
                }
            }
            throw new RuntimeException("Could not find any koodistoversio for koodiversio");
        }
        
    }
    
}
