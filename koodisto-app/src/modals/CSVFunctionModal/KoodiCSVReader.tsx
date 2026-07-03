import React from 'react';

import { FormattedMessage } from 'react-intl';
import Papa, { ParseResult } from 'papaparse';
import Button from 'virkailija-ui-components/Button';
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

export const KoodiCSVReader = <T extends object>({ onUploadAccepted }: Props<T>) => {
    const inputRef = React.useRef<HTMLInputElement>(null);
    const [acceptedFile, setAcceptedFile] = React.useState<File>();

    const openFileDialog = () => inputRef.current?.click();

    const parseFile = (file: File, event: DragEvent | Event) => {
        setAcceptedFile(file);
        Papa.parse<T>(file, {
            header: true,
            complete: (results) => onUploadAccepted?.(results, file, event),
        });
    };

    const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.currentTarget.files?.[0];
        if (file) {
            parseFile(file, event.nativeEvent);
        }
    };

    const handleDrop = (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        const file = event.dataTransfer.files[0];
        if (file) {
            parseFile(file, event.nativeEvent);
        }
    };

    const handleKeyDown = (event: React.KeyboardEvent<HTMLDivElement>) => {
        if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            openFileDialog();
        }
    };

    return (
        <div>
            <input ref={inputRef} type="file" accept=".csv,text/csv" hidden onChange={handleFileSelect} />
            <Reader>
                <Button type="button" onClick={openFileDialog}>
                    <FormattedMessage id={'LATAA_CVS_VALITSE_TIEDOSTO'} defaultMessage={'Valitse tiedosto'} />
                </Button>
                <AcceptedFile>{acceptedFile?.name}</AcceptedFile>
            </Reader>
            {!acceptedFile && (
                <UploadArea
                    role="button"
                    tabIndex={0}
                    onClick={openFileDialog}
                    onDrop={handleDrop}
                    onDragOver={(event) => event.preventDefault()}
                    onKeyDown={handleKeyDown}
                >
                    <FormattedMessage
                        id={'LATAA_CVS_VALITSE_TIEDOSTO_ALUE'}
                        defaultMessage={'Tai raahaa tiedosto tähän'}
                    />
                </UploadArea>
            )}
        </div>
    );
};
