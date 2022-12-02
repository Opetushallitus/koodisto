import React, { useMemo, useState } from 'react';
import { FormattedDate, FormattedMessage, useIntl } from 'react-intl';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { IconWrapper } from '../../components/IconWapper';
import styled from 'styled-components';
import { Link, useNavigate } from 'react-router-dom';
import { ListKoodisto, SelectOptionType, KoodistoRelation, PageSize } from '../../types';
import { koodistoListAtom } from '../../api/koodisto';
import { useAtom } from 'jotai';
import { ColumnDef, CellContext, Row, ColumnFiltersState } from '@tanstack/react-table';
import { sortBy } from 'lodash';
import { ButtonLabelPrefix } from '../Containers';
import { Table } from './Table';
import { atomWithStorage } from 'jotai/utils';

export const HeaderContainer = styled.div`
    display: flex;
    padding: 1rem 1rem;
    align-items: center;
    justify-content: space-between;
    border: 1px solid #cccccc;
`;
const HeaderContentDivider = styled.div`
    display: inline-flex;
    align-items: baseline;

    > * {
        &:first-child {
            padding-right: 1rem;
        }
    }
`;

const InfoText = styled.span`
    color: #666666;
`;

const TableCellText = styled.span`
    color: #0a789c;
`;

type KoodistoTableProps = {
    modal?: boolean;
    setSelected?: (selected: ListKoodisto[]) => void;
    oldRelations?: KoodistoRelation[];
    pageSize?: PageSize;
};
const koodistoFiltersAtom = atomWithStorage<ColumnFiltersState>('koodisto-app:koodistoFilters', []);
export const KoodistoTable: React.FC<KoodistoTableProps> = ({
    modal,
    setSelected,
    oldRelations = [],
    pageSize = 50,
}) => {
    const navigate = useNavigate();
    const [atomData] = useAtom(koodistoListAtom);
    const [koodistoFilters, setKoodistoFilters] = useAtom(koodistoFiltersAtom);
    const { formatMessage } = useIntl();
    const data = useMemo<ListKoodisto[]>(
        () =>
            sortBy(
                atomData.reduce(
                    (p, c) => [...(oldRelations?.find((a) => a.koodistoUri === c.koodistoUri) ? [] : [c]), ...p],
                    [] as ListKoodisto[]
                ),
                (o) => o.nimi
            ),
        [atomData, oldRelations]
    );
    const [filteredCount, setFilteredCount] = useState<number>(data.length);
    const columns = React.useMemo<ColumnDef<ListKoodisto>[]>(
        () => [
            {
                header: formatMessage({ id: 'TAULUKKO_KOODISTORYHMA_OTSIKKO', defaultMessage: 'Koodistoryhmä' }),
                columns: [
                    {
                        id: 'ryhmaUri',
                        header: '',
                        accessorFn: (values: ListKoodisto) => ({ label: values.ryhmaNimi, value: values.ryhmaUri }),
                        filterFn: (row, columnId, value: SelectOptionType[]) =>
                            !!value.find((a) => a.value === (row.getValue(columnId) as SelectOptionType).value) ||
                            value.length === 0,
                        sortingFn: (a: Row<ListKoodisto>, b: Row<ListKoodisto>) =>
                            a.original.ryhmaNimi?.localeCompare(b.original.ryhmaNimi || ''),
                        cell: (info) => (
                            <Link
                                title={info.row.original.ryhmaUri}
                                to={`/koodistoRyhma/${info.row.original.ryhmaUri}`}
                            >
                                {info.getValue().label}
                            </Link>
                        ),
                    },
                ],
            },
            {
                header: formatMessage({ id: 'TAULUKKO_NIMI_OTSIKKO', defaultMessage: 'Nimi' }),
                columns: [
                    {
                        id: 'koodistoUri',
                        header: '',
                        accessorFn: (values: ListKoodisto) =>
                            values.nimi ||
                            formatMessage(
                                {
                                    id: 'TAULUKKO_NIMI_PUUTTUU_KOODISTOLTA',
                                    defaultMessage: 'Nimi puuttuu {koodistoUri}',
                                },
                                { koodistoUri: values.koodistoUri }
                            ),
                        filterFn: (row, _, value: string) =>
                            row.original.koodistoUri?.toLowerCase().includes(value.toLowerCase()) ||
                            row.original.nimi?.toLowerCase().includes(value.toLowerCase()) ||
                            false,
                        meta: {
                            filterPlaceHolder: {
                                id: 'TAULUKKO_HAKU_APUTEKSTI',
                                defaultMessage: 'Haetaan {rows} koodistosta',
                            },
                        },
                        cell: (info) => (
                            <Link
                                title={info.row.original.koodistoUri}
                                to={`koodisto/view/${info.row.original.koodistoUri}/${info.row.original.versio}`}
                            >
                                {info.getValue()}
                            </Link>
                        ),
                    },
                ],
            },
            ...(!modal
                ? [
                      {
                          header: formatMessage({
                              id: 'TAULUKKO_VOIMASSA_ALKU_PVM_OTSIKKO',
                              defaultMessage: 'Voimassa alkaen',
                          }),
                          columns: [
                              {
                                  id: 'voimassaAlkuPvm',
                                  accessorKey: 'voimassaAlkuPvm',
                                  header: '',
                                  enableColumnFilter: false,
                                  sortingFn: 'datetime',
                                  cell: (info: CellContext<ListKoodisto, unknown>) =>
                                      info.row.original.voimassaAlkuPvm && (
                                          <TableCellText>
                                              <FormattedDate value={info.row.original.voimassaAlkuPvm} />
                                          </TableCellText>
                                      ),
                              },
                          ],
                      },
                      {
                          header: formatMessage({
                              id: 'TAULUKKO_VOIMASSA_LOPPU_PVM_OTSIKKO',
                              defaultMessage: 'Voimassa asti',
                          }),
                          columns: [
                              {
                                  id: 'voimassaLoppuPvm',
                                  header: '',
                                  accessorKey: 'voimassaLoppuPvm',
                                  enableColumnFilter: false,
                                  sortingFn: 'datetime',
                                  cell: (info: CellContext<ListKoodisto, unknown>) =>
                                      info.row.original.voimassaLoppuPvm && (
                                          <TableCellText>
                                              <FormattedDate value={info.row.original.voimassaLoppuPvm} />
                                          </TableCellText>
                                      ),
                              },
                          ],
                      },
                      {
                          header: formatMessage({ id: 'TAULUKKO_KOODIMAARA', defaultMessage: 'Koodien lkm' }),
                          columns: [
                              {
                                  id: 'koodiCount',
                                  header: '',
                                  accessorKey: 'koodiCount',
                                  enableColumnFilter: false,
                                  cell: (info: CellContext<ListKoodisto, unknown>) => (
                                      <TableCellText>{info.row.original.koodiCount}</TableCellText>
                                  ),
                              },
                          ],
                      },
                  ]
                : []),
            {
                header: formatMessage({ id: 'TAULUKKO_VERSIO', defaultMessage: 'Versio' }),
                columns: [
                    {
                        id: 'versio',
                        header: '',
                        accessorKey: 'versio',
                        enableColumnFilter: false,
                        sortingFn: 'basic',
                        cell: (info: CellContext<ListKoodisto, unknown>) =>
                            info.row.original.versio && <TableCellText>{info.row.original.versio}</TableCellText>,
                    },
                ],
            },
        ],
        [formatMessage, modal]
    );
    return (
        <>
            {!modal && (
                <HeaderContainer>
                    <HeaderContentDivider>
                        <FormattedMessage
                            id={'KOODISTO_TAULUKKO_OTSIKKO'}
                            defaultMessage={'Koodistot ({filteredCount} / {totalCount})'}
                            values={{ filteredCount, totalCount: data.length }}
                            tagName={'h2'}
                        />
                        <InfoText>
                            <FormattedMessage
                                id={'KOODISTO_TAULUKKO_KUVAUS_OTSIKKO'}
                                defaultMessage={'Voit rajata koodistoryhmällä tai nimellä'}
                            />
                        </InfoText>
                    </HeaderContentDivider>
                    <Button onClick={() => navigate('/koodisto/edit/')}>
                        <ButtonLabelPrefix>
                            <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                        </ButtonLabelPrefix>
                        <FormattedMessage id={'TAULUKKO_LISAA_KOODISTO_BUTTON'} defaultMessage={'Luo uusi koodisto'} />
                    </Button>
                </HeaderContainer>
            )}
            <Table<ListKoodisto>
                columns={columns}
                data={data}
                modal={modal}
                onFilter={(rows) => setFilteredCount(rows.length)}
                setSelected={setSelected}
                pageSize={pageSize}
                initialFilterState={koodistoFilters}
                setStoredFilterState={setKoodistoFilters}
            />
        </>
    );
};
