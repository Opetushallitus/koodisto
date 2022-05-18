import Modal from '../Modal/Modal';
import { FormattedMessage, useIntl } from 'react-intl';
import CSVReaderModal from '../CSVReaderModal/CSVReaderModal';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import * as React from 'react';
import { useMemo, useState } from 'react';
import Table from '../Table/Table';
import { Column } from 'react-table';
import { CsvKoodiObject, MessageFormatter } from '../../types/types';
import { getHeaders, mapCsvToKoodi, mapHeadersToColumns, validData } from './uploadCsv';
import { batchUpsertKoodi, useKoodisto } from '../../api/koodisto';
import Loading from '../pages/Loading/Loading';
import { danger, success } from '../Notification/Notification';
import downloadCsv from '../../utils/downloadCsv';
import styled from 'styled-components';

const DownloadContainer = styled.div`
    padding-bottom: 1rem;
    margin-bottom: 1rem;
    border-bottom: 1px solid #ccc;
    > * {
        :not(:last-child) {
            margin-bottom: 0.5rem;
        }
    }
`;
const DownloadContainerItem = styled.div``;
const UploadContainer = styled.div`
    padding-bottom: 1rem;
    margin-bottom: 1rem;
    border-bottom: 1px solid #ccc;
    > * {
        :not(:last-child) {
            margin-bottom: 0.5rem;
        }
    }
`;
const UploadContainerItem = styled.div``;
const Footer = styled.div`
    > * {
        :not(:last-child) {
            margin-right: 0.5rem;
        }
    }
`;
type Props = {
    koodistoUri: string;
    koodistoVersio?: number;
    closeUploader: () => void;
};

const onUpload = ({
    data,
    koodistoUri,
    setCsvKoodiArray,
}: {
    data: CsvKoodiObject[];
    koodistoUri: string;
    setCsvKoodiArray: (csvKoodiArray: CsvKoodiObject[]) => void;
}): void => {
    const wrongKoodistoUri = data?.find((a) => {
        return a.koodistoUri !== koodistoUri;
    });
    if (wrongKoodistoUri) {
        setCsvKoodiArray([]);
        danger({
            title: (
                <FormattedMessage
                    id={'CSV_UPLOAD_INVALID_KOODISTORI_TITLE'}
                    defaultMessage={'Virheellinen koodistoUri tiedostossa.'}
                />
            ),
            message: (
                <FormattedMessage
                    id={'CSV_UPLOAD_INVALID_KOODISTORI_MESSAGE'}
                    defaultMessage={'Tiedostosta löytyi koodistoUri {wrongKoodistoUri}'}
                    values={{ wrongKoodistoUri: wrongKoodistoUri.koodistoUri }}
                />
            ),
        });
    } else {
        setCsvKoodiArray(data);
    }
};

const persistData = async ({
    data,
    koodistoUri,
    formatMessage,
    setSaving,
}: {
    data: CsvKoodiObject[];
    koodistoUri: string;
    formatMessage: MessageFormatter;
    setSaving: (saving: boolean) => void;
}) => {
    const koodi = data.map(mapCsvToKoodi);
    setSaving(true);
    const result = await batchUpsertKoodi(koodistoUri, koodi);
    if (result) {
        success({
            title: formatMessage({ id: 'CSV_UPLOAD_SUCCESS', defaultMessage: 'Tuonti onnistui' }),
            message: formatMessage(
                {
                    id: 'CSV_UPLOAD_SUCCESS_MESSAGE',
                    defaultMessage: '{koodistoUri} koodiston koodit tuotiin onnistuneesti.',
                },
                { koodistoUri: result.koodistoUri }
            ),
        });
    }
};

const CSVUploader: React.FC<Props> = ({ koodistoUri, koodistoVersio, closeUploader }) => {
    const [csvKoodiArray, setCsvKoodiArray] = useState<CsvKoodiObject[]>([]);
    const [saving, setSaving] = useState<boolean>(false);
    const now = useMemo<number>(() => (koodistoUri.length > 0 ? Date.now() : 0), [koodistoUri]);
    const { formatMessage } = useIntl();
    const { data, loading } = useKoodisto(koodistoUri, now);
    const headers = useMemo<(keyof CsvKoodiObject)[]>(() => [...getHeaders(csvKoodiArray || [])], [csvKoodiArray]);
    const dataMemo = useMemo<CsvKoodiObject[]>(
        () =>
            csvKoodiArray?.map((a) => ({
                ...a,
                newRow: data?.find((b) => b.koodiArvo === a.koodiArvo) === undefined,
            })) || [],
        [csvKoodiArray, data]
    );

    const columns = React.useMemo<Column<CsvKoodiObject>[]>(
        () => mapHeadersToColumns({ headers: ['newRow' as keyof CsvKoodiObject, ...headers], formatMessage }),
        [formatMessage, headers]
    );
    if (loading) return <Loading />;
    return (
        <Modal
            body={
                <>
                    <DownloadContainer>
                        <DownloadContainerItem>
                            <FormattedMessage
                                id={'LATAA_CSV_LATAA_KUVAUS'}
                                defaultMessage={'Lataa koodisto omalle koneelle'}
                                tagName={'h2'}
                            />
                        </DownloadContainerItem>
                        <DownloadContainerItem>
                            <Button
                                onClick={() => downloadCsv({ koodistoUri, koodistoVersio })}
                                name={`${koodistoUri}-download`}
                            >
                                <FormattedMessage id={'LATAA_CSV_LATAA'} defaultMessage={'Lataa CSV'} />
                            </Button>
                        </DownloadContainerItem>
                    </DownloadContainer>
                    <UploadContainer>
                        <UploadContainerItem>
                            <FormattedMessage
                                id={'LATAA_CSV_TUO_KUVAUS'}
                                defaultMessage={'Tuo koodi omalta koneelta'}
                                tagName={'h2'}
                            />
                        </UploadContainerItem>
                        <UploadContainerItem>
                            <CSVReaderModal<CsvKoodiObject>
                                onUploadAccepted={(results) =>
                                    onUpload({ data: results.data, setCsvKoodiArray, koodistoUri })
                                }
                            />
                        </UploadContainerItem>
                        {(saving && <Loading />) || (
                            <UploadContainerItem>
                                {!!csvKoodiArray.length && <Table columns={columns} data={dataMemo} />}
                            </UploadContainerItem>
                        )}
                    </UploadContainer>
                </>
            }
            footer={
                <Footer>
                    <Button
                        disabled={!validData(csvKoodiArray) || saving}
                        onClick={async () => {
                            await persistData({ setSaving, data: dataMemo, koodistoUri, formatMessage });
                            setSaving(false);
                            closeUploader();
                        }}
                    >
                        <FormattedMessage id={'LATAA_CSV_TALLENNA'} defaultMessage={'Tallenna'} />
                    </Button>
                    <Button onClick={closeUploader}>
                        <FormattedMessage id={'LATAA_CSV_PERUUTA'} defaultMessage={'Peruuta'} />
                    </Button>
                </Footer>
            }
            onClose={closeUploader}
        />
    );
};
export default CSVUploader;
