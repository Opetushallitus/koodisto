package fi.vm.sade.koodisto.ui.util;

import com.vaadin.data.Container;
import com.vaadin.data.util.ItemSorter;

import fi.vm.sade.koodisto.service.types.common.KoodiType;

public class KoodiTypeCaptionSorter implements ItemSorter {

    @Override
    public void setSortProperties(Container.Sortable sortable, Object[] objects, boolean[] booleans) {
    }

    @Override
    public int compare(Object left, Object right) {

        KoodiType leftKoodiType = (KoodiType) left;
        KoodiType rightKoodiType = (KoodiType) right;

        return new ComparableCaption(KoodiTypeUtil.extractNameForKoodi(leftKoodiType)).compareTo(new ComparableCaption(KoodiTypeUtil
                .extractNameForKoodi(rightKoodiType)));

    }

    private class ComparableCaption implements Comparable<ComparableCaption> {

        private Integer number;
        private String name;

        private boolean hasNumber = false;

        public ComparableCaption(String caption) {

            caption = caption.trim();

            String numberString = extractDigitsFromBeginning(caption);
            if (numberString.length() > 0) {
                this.number = Integer.valueOf(numberString);
                this.hasNumber = true;
            }

            if (hasNumber) {
                this.name = caption.substring(caption.lastIndexOf(numberString) + 1, caption.length());
            } else {
                this.name = caption;
            }
        }

        private String extractDigitsFromBeginning(String s) {

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < s.length(); i++) {

                char c = s.charAt(i);

                if (Character.isDigit(c)) {
                    builder.append(c);
                } else {
                    break;
                }

            }

            return builder.toString();
        }

        @Override
        public int compareTo(ComparableCaption right) {

            int result;

            if (this.hasNumber && !right.hasNumber) {
                return -1;
            } else if (!this.hasNumber && right.hasNumber) {
                return 1;
            } else if (!this.hasNumber && !right.hasNumber) {
                result = 0;
            } else {
                result = this.number.compareTo(right.number);
            }

            if (result == 0) {
                result = this.name.compareTo(right.name);
            }

            return result;
        }

    }
}
