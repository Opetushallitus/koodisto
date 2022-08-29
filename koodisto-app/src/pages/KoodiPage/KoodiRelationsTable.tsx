import React, { useMemo, useCallback } from 'react';
import { useIntl, FormattedMessage } from 'react-intl';
import { Link } from 'react-router-dom';
import { Table } from '../../components/Table';
import { translateMultiLocaleText, metadataToMultiLocaleText } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLocaleAtom } from '../../api/kayttooikeus';
import type { KoodiRelation, Koodi, KoodiList } from '../../types';
import { KoodistoRelation } from '../../types';
import { ColumnDef, CellContext } from '@tanstack/react-table';
import { UseFieldArrayReturn } from 'react-hook-form';
import { KoodiSuhdeModal } from './KoodiSuhdeModal';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { IconWrapper } from '../../components/IconWapper';
import { StyledPopup } from '../../components/Modal/Modal';
import { ButtonLabelPrefix } from '../../components/Containers';

type RelationTableProps = {
    editable: boolean;
    relations: KoodiRelation[];
    fieldArrayReturn?: UseFieldArrayReturn<Koodi>;
    relationSources?: { koodistoUri: string; versio: number }[];
};
const RemoveSuhdeButton: React.FC<{ onClick: (e: React.ChangeEvent<HTMLInputElement>) => void }> = ({ onClick }) => {
    return (
        <Button name={'TAULUKKO_POISTA_KOODISUHTEITA_BUTTON'} onClick={onClick} variant={'text'}>
            <ButtonLabelPrefix>
                <IconWrapper icon={'ci:trash-full'} inline={true} height={'1.2rem'} />
            </ButtonLabelPrefix>
        </Button>
    );
};
export const KoodiRelationsTable: React.FC<RelationTableProps> = ({
    relations,
    fieldArrayReturn,
    editable,
    relationSources = [],
}) => {
    const { formatMessage } = useIntl();
    const [locale] = useAtom(casMeLocaleAtom);
    const data = useMemo<KoodiRelation[]>(() => {
        return [...relations];
    }, [relations]);
    const removeKoodiFromRelations = useCallback(
        (index: number) => {
            fieldArrayReturn && fieldArrayReturn.remove(index);
        },
        [fieldArrayReturn]
    );
    const addNewKoodiToRelations = useCallback(
        (koodi: KoodiList[]) => {
            fieldArrayReturn &&
                fieldArrayReturn.replace([
                    ...relations,
                    ...koodi.map((a: KoodiList) => ({
                        koodistoNimi: { fi: a.koodistoNimi || '', sv: a.koodistoNimi || '', en: a.koodistoNimi || '' },
                        nimi: metadataToMultiLocaleText(a.metadata, 'nimi'),
                        kuvaus: metadataToMultiLocaleText(a.metadata, 'kuvaus'),
                        koodiUri: a.koodiUri,
                        koodiVersio: a.versio,
                    })),
                ]);
        },
        [fieldArrayReturn, relations]
    );
    const columns = useMemo<ColumnDef<KoodiRelation>[]>(
        () => [
            {
                id: 'koodisto',
                header: formatMessage({ id: 'TAULUKKO_KOODISTO_OTSIKKO', defaultMessage: 'Koodisto' }),
                accessorFn: (relation: KoodiRelation) =>
                    translateMultiLocaleText({
                        multiLocaleText: relation.koodistoNimi,
                        locale,
                        defaultValue: relation.koodistoNimi?.fi || '',
                    }),
            },
            {
                id: 'nimi',
                enableColumnFilter: false,
                header: formatMessage({ id: 'TAULUKKO_NIMI_OTSIKKO', defaultMessage: 'Nimi' }),
                cell: (info) => (
                    <Link to={`/koodi/view/${info.row.original.koodiUri}/${info.row.original.koodiVersio}`}>
                        {translateMultiLocaleText({
                            multiLocaleText: info.row.original.nimi,
                            locale,
                            defaultValue: info.row.original.koodiUri,
                        })}
                    </Link>
                ),
            },
            {
                id: 'versio',
                enableColumnFilter: false,
                header: formatMessage({ id: 'TAULUKKO_VERSIO_OTSIKKO', defaultMessage: 'Versio' }),
                accessorFn: (relation: KoodiRelation) => relation.koodiVersio,
            },
            {
                id: 'kuvaus',
                enableColumnFilter: false,
                header: formatMessage({ id: 'TAULUKKO_VERSIO_KUVAUS', defaultMessage: 'Kuvaus' }),
                accessorFn: (relation: KoodiRelation) =>
                    translateMultiLocaleText({
                        multiLocaleText: relation.kuvaus,
                        locale,
                        defaultValue: relation.koodiUri,
                    }),
            },
            ...((editable && [
                {
                    header: '',
                    id: 'poista',
                    columns: [
                        {
                            id: 'poista',
                            header: '',
                            enableColumnFilter: false,
                            cell: (info: CellContext<KoodistoRelation, never>) => (
                                <RemoveSuhdeButton onClick={() => removeKoodiFromRelations(info.row.index)} />
                            ),
                        },
                    ],
                },
            ]) ||
                []),
        ],
        [editable, formatMessage, locale, removeKoodiFromRelations]
    );

    const suhdeModal = useCallback(
        (close) => (
            <KoodiSuhdeModal
                relationSources={relationSources || []}
                save={addNewKoodiToRelations}
                close={() => {
                    close();
                }}
            />
        ),
        [addNewKoodiToRelations, relationSources]
    );
    return (
        <>
            <Table<KoodiRelation> columns={columns} data={data} pageSize={20} />{' '}
            {editable && (
                <StyledPopup
                    trigger={
                        <Button
                            disabled={relationSources.length === 0}
                            name={'TAULUKKO_LISAA_KOODISUHTEITA_BUTTON'}
                            onClick={(e: React.ChangeEvent<HTMLInputElement>) => console.log(e)}
                            variant={'text'}
                        >
                            <ButtonLabelPrefix>
                                <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                            </ButtonLabelPrefix>
                            <FormattedMessage id={'TAULUKKO_LISAA_KOODEJA_BUTTON'} defaultMessage={'Lisää koodeja'} />
                        </Button>
                    }
                    modal
                >
                    {suhdeModal}
                </StyledPopup>
            )}
        </>
    );
};
