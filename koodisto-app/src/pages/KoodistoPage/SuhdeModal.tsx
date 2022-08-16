import React, { useState } from 'react';
import { Modal, Footer } from '../../components/Modal';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { FormattedMessage } from 'react-intl';
import KoodistoTable from '../KoodistoTablePage/KoodistoTable';
import { ListKoodisto, KoodistoRelation } from '../../types';
type SuhdeModalProps = { close: () => void; save: (toAdd: ListKoodisto[]) => void; oldRelations: KoodistoRelation[] };
export const SuhdeModal: React.FC<SuhdeModalProps> = ({ close, save, oldRelations }) => {
    const [selected, setSelected] = useState<ListKoodisto[]>([]);
    return (
        <Modal
            header={<FormattedMessage id={'SUHDEMODAL_HEADER'} defaultMessage={'Valitse koodistot'} />}
            body={<KoodistoTable modal setSelected={setSelected} oldRelations={oldRelations} />}
            footer={
                <Footer>
                    <Button
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
            }
            onClose={close}
        ></Modal>
    );
};
