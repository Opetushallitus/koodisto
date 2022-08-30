import React, { useState } from 'react';
import { Modal } from '../../components/Modal';
import { FormattedMessage } from 'react-intl';
import { ListKoodisto, KoodistoRelation } from '../../types';
import { KoodistoTable } from '../../components/Table';
import { SuhdeModalFooter } from '../../modals/SuhdeModal';

type SuhdeModalProps = { close: () => void; save: (toAdd: ListKoodisto[]) => void; oldRelations: KoodistoRelation[] };

export const KoodistoSuhdeModal: React.FC<SuhdeModalProps> = ({ close, save, oldRelations }) => {
    const [selected, setSelected] = useState<ListKoodisto[]>([]);
    return (
        <Modal
            header={<FormattedMessage id={'KOODISTO_SUHDE_MODAL_HEADER'} defaultMessage={'Valitse koodistot'} />}
            body={<KoodistoTable modal setSelected={setSelected} oldRelations={oldRelations} pageSize={10} />}
            footer={<SuhdeModalFooter selected={selected} close={close} save={save} />}
            onClose={close}
        ></Modal>
    );
};
