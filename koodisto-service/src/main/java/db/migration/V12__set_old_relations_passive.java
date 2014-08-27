package db.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

import fi.vm.sade.koodisto.model.Tila;

public class V12__set_old_relations_passive implements SpringJdbcMigration {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(V12__set_old_relations_passive.class);
    
    @Override
    public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {
        LOGGER.info("Starting migration of setting old relations to passive where applicable");
        handleCodesRelations(jdbcTemplate);
        handleCodeElementsRelations(jdbcTemplate);
    }

    private void handleCodeElementsRelations(JdbcTemplate jdbcTemplate) {
        List<RelationDto> codeElementRelations = jdbcTemplate.query("SELECT * FROM koodinsuhde ks", new RowMapper<RelationDto>() {

            @Override
            public RelationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new RelationDto(rs.getLong("id"), rs.getLong("alakoodiversio_id"), rs.getLong("ylakoodiversio_id"));
            }
            
        });
        
        final List<UriVersionDto> uriVersions = jdbcTemplate.query("SELECT kv.*, k.koodiuri FROM koodiversio kv, koodi k WHERE kv.koodi_id = k.id",
                new RowMapper<UriVersionDto>() {

                    @Override
                    public UriVersionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new UriVersionDto(rs.getLong("id"), rs.getInt("versio"), rs.getString("koodiuri"), Tila.valueOf(rs.getString("tila")));
                    }
        });
        Collection<RelationDto> relationsToUpdate = Collections2.filter(codeElementRelations, new RelationsToPassivePredicate(uriVersions));
        //Collection<RelationDto> relationsToUpdate = Collections2.filter(codeElementRelations, 
                //new CodeElementRelationsToPassivePredicate(jdbcTemplate, uriVersions));
        LOGGER.info("Updating " + relationsToUpdate.size() + "  koodinsuhde rows to passive");
        updateCodeElementRelationsToPassive(jdbcTemplate, relationsToUpdate);
    }


    private void handleCodesRelations(final JdbcTemplate jdbcTemplate) {
        List<RelationDto> codesRelations = jdbcTemplate.query("SELECT * FROM koodistonsuhde ks", new RowMapper<RelationDto>() {

            @Override
            public RelationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new RelationDto(rs.getLong("id"), rs.getLong("alakoodistoversio_id"), rs.getLong("ylakoodistoversio_id"));
            }
            
        });
        
        List<UriVersionDto> uriVersions = jdbcTemplate.query("SELECT kv.*, k.koodistouri FROM koodistoversio kv, koodisto k WHERE kv.koodisto_id = k.id",
                new RowMapper<UriVersionDto>() {

                    @Override
                    public UriVersionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new UriVersionDto(rs.getLong("id"), rs.getInt("versio"), rs.getString("koodistouri"), Tila.valueOf(rs.getString("tila")));
                    }
            
        });
        
        Collection<RelationDto> relationsToUpdate = Collections2.filter(codesRelations, new RelationsToPassivePredicate(uriVersions));
        LOGGER.info("Updating " + relationsToUpdate.size() + "  koodistonsuhde rows to passive");
        updateCodesRelationsToPassive(jdbcTemplate, relationsToUpdate);
    }
    
    private void updateCodeElementRelationsToPassive(JdbcTemplate jdbcTemplate, Collection<RelationDto> relationsToUpdate) {
        for (RelationDto relationDto : relationsToUpdate) {
            jdbcTemplate.update("UPDATE koodinsuhde SET ylakoodistapassiivinen = ?, alakoodistapassiivinen = ? WHERE id = ?", 
                    relationDto.upperPassive, relationDto.lowerPassive, relationDto.id);
        }
    }
    
    private void updateCodesRelationsToPassive(JdbcTemplate jdbcTemplate, Collection<RelationDto> relationsToUpdate) {
        for (RelationDto relationDto : relationsToUpdate) {
            jdbcTemplate.update("UPDATE koodistonsuhde SET ylakoodistostapassiivinen = ?, alakoodistostapassiivinen = ? WHERE id = ?", 
                    relationDto.upperPassive, relationDto.lowerPassive, relationDto.id);
        }
        LOGGER.info(relationsToUpdate.size() + " koodistonsuhde rows was succesfully set to passive");
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
    
    private static class UriVersionDto {
        private final Long id;
        private final Integer versio;
        private final String uri;
        private final Tila tila;
        
        private UriVersionDto(long id, int versio, String uri, Tila tila) {
            this.id = id;
            this.versio = versio;
            this.uri = uri;
            this.tila = tila;
        }
    }
    
    private class RelationsToPassivePredicate implements Predicate<RelationDto> {
        
        private final List<UriVersionDto> uriVersions;
        
        public RelationsToPassivePredicate(List<UriVersionDto> uriVersions) {
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
            Collection<UriVersionDto> matchingVersions = Collections2.filter(uriVersions, new Predicate<UriVersionDto>() {

                @Override
                public boolean apply(UriVersionDto input) {
                    return input.equals(uriVersion.uri);
                }
            });
            return isRelationActive(uriVersion, matchingVersions);
        }

        protected boolean isRelationActive(UriVersionDto uriVersion, Collection<UriVersionDto> matchingVersions) {
            UriVersionDto latest = getLatestVersion(uriVersion, matchingVersions);
            return !Tila.PASSIIVINEN.equals(latest.tila) 
                    && (latest == uriVersion 
                    || (latest.versio == uriVersion.versio + 1 && Tila.LUONNOS.equals(latest.tila)));
        }

        private UriVersionDto getLatestVersion(UriVersionDto uriVersion, Collection<UriVersionDto> matchingVersions) {
            UriVersionDto latest = uriVersion;
            for (UriVersionDto uriVersionDto : matchingVersions) {
                latest = uriVersionDto.versio > latest.versio ? uriVersionDto : latest;
            }
            return latest;
        }
    }
    
    private class CodeElementRelationsToPassivePredicate extends RelationsToPassivePredicate {
        
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
            List<KoodistoVersio> codesVersions = jdbcTemplate.query("SELECT koodistoV.id, koodistoV.tila, koodistoV.versio "
                    + "FROM koodistoversio koodistoV, koodi koodi, koodisto "
                    + "WHERE koodistoV.koodisto_id = koodisto.id AND koodisto.id = koodi.koodisto_id AND koodi.koodiuri = '" 
                    + latestVersion.uri + "'", 
                    new RowMapper<KoodistoVersio>() {

                        @Override
                        public KoodistoVersio mapRow(ResultSet rs, int rowNum) throws SQLException {
                            List<Long> koodiVersios = jdbcTemplate.query("SELECT kvkv.koodiversio_id FROM koodistoversio_koodiversio kvkv WHERE kvkv.koodistoversio_id = '" + rs.getLong("id") + "'",
                                    new RowMapper<Long>() {

                                        @Override
                                        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                                            return rs.getLong("koodiversio_id");
                                        }
                                
                            });
                            return new KoodistoVersio(Tila.valueOf(rs.getString("tila")), rs.getInt("versio"), koodiVersios);
                        }
                        
                    });
            Collections.sort(codesVersions);
            KoodistoVersio latestCodes = codesVersions.get(codesVersions.size() - 1);
            KoodistoVersio secondLatestCodes = codesVersions.size() > 1 ? codesVersions.get(codesVersions.size() - 2) : null;
            return latestCodes.koodiVersios.contains(latestVersion.id) 
                    || (Tila.LUONNOS.equals(latestCodes.tila) 
                            && secondLatestCodes != null && secondLatestCodes.koodiVersios.contains(latestVersion.id));
        }
        
        private class KoodistoVersio implements Comparable<KoodistoVersio> {
            private final Tila tila;
            private final Integer versio;
            private final List<Long> koodiVersios;
            
            public KoodistoVersio(Tila tila, Integer versio, List<Long> koodiVersios) {
                this.tila = tila;
                this.versio = versio;
                this.koodiVersios = koodiVersios;
            }

            @Override
            public int compareTo(KoodistoVersio arg0) {
                return versio.compareTo(arg0.versio);
            }
            
        }
    }
    
}
