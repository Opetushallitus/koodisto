import { Footer } from '../../components/Modal';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { FormattedMessage } from 'react-intl';
import React from 'react';

export const SuhdeModalFooter = <T,>({
    close,
    save,
    selected,
}: {
    close: () => void;
    save: (toAdd: T[]) => void;
    selected: T[];
}) => (
    <Footer>
        <Button
            disabled={selected.length === 0}
            onClick={() => {
                save(selected);
                close();
            }}
            name={'SUHDEMODAL_LISAA'}
        >
            <FormattedMessage
                id={'SUHDEMODAL_LISAA'}
                defaultMessage={'Lisää valitut ({chosen})'}
                values={{ chosen: selected.length }}
            />
        </Button>
        <Button onClick={close} name={'SUHDEMODAL_SULJE'}>
            <FormattedMessage id={'SUHDEMODAL_SULJE'} defaultMessage={'Sulje'} />
        </Button>
    </Footer>
);
