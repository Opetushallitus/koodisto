import React from 'react';

import { useCSVReader } from 'react-papaparse';
import { FormattedMessage } from 'react-intl';
import { ParseResult } from 'papaparse';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import styled from 'styled-components';
const Reader = styled.div`
    display: flex;
    flex-direction: row;
    padding-bottom: 1rem;
`;
const AcceptedFile = styled.div`
    border: 1px solid #ccc;
    width: 30rem;
    margin-left: 1rem;
    padding-left: 0.5rem;
    border-radius: 4px;
    display: flex;
    justify-content: center;
    align-content: center;
    flex-direction: column;
`;
const UploadArea = styled.div`
    min-height: 8rem;
    background-color: #eee;
    border: dotted;
    display: flex;
    text-align: center;
    justify-content: center;
    align-content: center;
    flex-direction: column;
`;

type Props<T> = {
    onUploadAccepted?: (data: ParseResult<T>, file?: File, event?: DragEvent | Event) => void;
};

const CSVReaderModal = <T extends object>({ onUploadAccepted }: Props<T>) => {
    const { CSVReader } = useCSVReader();
    return (
        <CSVReader onUploadAccepted={onUploadAccepted} config={{ header: true }}>
            {({
                getRootProps,
                acceptedFile,
            }: {
                getRootProps: () => never;
                acceptedFile: { name: string };
                getRemoveFileProps: () => unknown;
                Remove: React.FC<{ color: unknown }>;
            }) => (
                <div>
                    <Reader>
                        <Button {...getRootProps()}>
                            <FormattedMessage id={'LATAA_CVS_VALITSE_TIEDOSTO'} defaultMessage={'Valitse tiedosto'} />
                        </Button>
                        <AcceptedFile>{acceptedFile && acceptedFile.name}</AcceptedFile>
                    </Reader>
                    {!acceptedFile && (
                        <UploadArea {...getRootProps()}>
                            <FormattedMessage
                                id={'LATAA_CVS_VALITSE_TIEDOSTO_ALUE'}
                                defaultMessage={'Tai raahaa tiedosto tähän'}
                            />
                        </UploadArea>
                    )}
                </div>
            )}
        </CSVReader>
    );
};
export default CSVReaderModal;
