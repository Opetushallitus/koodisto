package fi.vm.sade.koodisto.ui.koodi.subkoodi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;

public class KoodiRelations {

    private Map<KoodiRelation, Set<KoodiUriAndVersioType>> toAdd = new HashMap<KoodiRelations.KoodiRelation, Set<KoodiUriAndVersioType>>();
    private Map<KoodiRelation, Set<KoodiUriAndVersioType>> toRemove = new HashMap<KoodiRelations.KoodiRelation, Set<KoodiUriAndVersioType>>();

    public void addRelationToAdd(KoodiUriAndVersioType ylakoodi, KoodiUriAndVersioType alakoodi, SuhteenTyyppiType suhteenTyyppi) {
        KoodiRelation relation = new KoodiRelation();
        relation.setYlakoodi(ylakoodi);
        relation.setSuhteenTyyppi(suhteenTyyppi);

        Set<KoodiUriAndVersioType> alakoodis = null;
        if (!toAdd.containsKey(relation)) {
            alakoodis = new HashSet<KoodiUriAndVersioType>();
            toAdd.put(relation, alakoodis);
        } else {
            alakoodis = toAdd.get(relation);
        }

        alakoodis.add(alakoodi);
    }

    public Map<KoodiRelation, Set<KoodiUriAndVersioType>> getToAdd() {
        return toAdd;
    }

    public Map<KoodiRelation, Set<KoodiUriAndVersioType>> getToRemove() {
        return toRemove;
    }

    public void addRelationToRemove(KoodiUriAndVersioType ylakoodi, KoodiUriAndVersioType alakoodi, SuhteenTyyppiType suhteenTyyppi) {
        KoodiRelation relation = new KoodiRelation();
        relation.setYlakoodi(ylakoodi);
        relation.setSuhteenTyyppi(suhteenTyyppi);

        Set<KoodiUriAndVersioType> alakoodis = null;
        if (!toRemove.containsKey(relation)) {
            alakoodis = new HashSet<KoodiUriAndVersioType>();
            toRemove.put(relation, alakoodis);
        } else {
            alakoodis = toRemove.get(relation);
        }

        alakoodis.add(alakoodi);
    }

    public void addRelationsToAdd(KoodiUriAndVersioType ylakoodi, Set<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi) {
        KoodiRelation relation = new KoodiRelation();
        relation.setYlakoodi(ylakoodi);
        relation.setSuhteenTyyppi(suhteenTyyppi);

        Set<KoodiUriAndVersioType> existingAlakoodis = null;
        if (!toAdd.containsKey(relation)) {
            existingAlakoodis = new HashSet<KoodiUriAndVersioType>();
            toAdd.put(relation, existingAlakoodis);
        } else {
            existingAlakoodis = toAdd.get(relation);
        }

        existingAlakoodis.addAll(alakoodis);

    }

    public void addRelationsToRemove(KoodiUriAndVersioType ylakoodi, Set<KoodiUriAndVersioType> alakoodis, SuhteenTyyppiType suhteenTyyppi) {
        KoodiRelation relation = new KoodiRelation();
        relation.setYlakoodi(ylakoodi);
        relation.setSuhteenTyyppi(suhteenTyyppi);

        Set<KoodiUriAndVersioType> existingAlakoodis = null;
        if (!toRemove.containsKey(relation)) {
            existingAlakoodis = new HashSet<KoodiUriAndVersioType>();
            toRemove.put(relation, existingAlakoodis);
        } else {
            existingAlakoodis = toRemove.get(relation);
        }

        existingAlakoodis.addAll(alakoodis);

    }

    public static class KoodiRelation {
        private KoodiUriAndVersioType ylakoodi;
        private SuhteenTyyppiType suhteenTyyppi;

        public KoodiUriAndVersioType getYlakoodi() {
            return ylakoodi;
        }

        public void setYlakoodi(KoodiUriAndVersioType ylakoodi) {
            this.ylakoodi = ylakoodi;
        }

        public SuhteenTyyppiType getSuhteenTyyppi() {
            return suhteenTyyppi;
        }

        public void setSuhteenTyyppi(SuhteenTyyppiType suhteenTyyppi) {
            this.suhteenTyyppi = suhteenTyyppi;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((suhteenTyyppi == null) ? 0 : suhteenTyyppi.hashCode());
            result = prime * result + ((ylakoodi == null) ? 0 : ylakoodi.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            KoodiRelation other = (KoodiRelation) obj;
            if (suhteenTyyppi != other.suhteenTyyppi) {
                return false;
            }
            if (ylakoodi == null) {
                if (other.ylakoodi != null) {
                    return false;
                }
            } else if (!ylakoodi.equals(other.ylakoodi)) {
                return false;
            }
            return true;
        }
    }
}
