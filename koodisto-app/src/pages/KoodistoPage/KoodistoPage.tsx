import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { FormattedMessage, useIntl } from 'react-intl';
import { IconWrapper } from '../../components/IconWapper';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { Link, useNavigate, useParams } from 'react-router-dom';
import Select from '@opetushallitus/virkailija-ui-components/Select';
import { fetchPageKoodisto } from '../../api/koodisto';
import { translateMetadata } from '../../utils';
import { PageKoodisto, SelectOptionType } from '../../types';
import { Loading } from '../../components/Loading';
import InfoFields from './InfoFields';
import KoodistoPageAccordion from './KoodistoPageAccordion';
import { CSVFunctionModal } from '../../modals/CSVFunctionModal';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';

const MainContainer = styled.div`
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    box-sizing: border-box;
    background-color: #ffffff;
    padding: 0 15rem 0 15rem;
`;
const MainHeaderContainer = styled.div`
    display: inline-flex;
    flex-direction: row;
    align-items: baseline;
    padding: 0 15rem 0 15rem;
    justify-content: space-between;
`;

const MainHeaderButtonsContainer = styled.div`
    display: flex;
    flex-direction: column;

    > * {
        :not(:last-child) {
            margin: 0 0 1rem 0;
        }
    }
`;

const KoodistoPathContainer = styled.div`
    height: 3rem;
    display: flex;
    align-items: center;
    padding-left: 15rem;
    justify-content: flex-start;
    min-width: 3rem;
`;

const HeadingDivider = styled.div`
    display: flex;
    align-items: center;

    > * {
        &:first-child {
            margin-right: 3rem;
        }
    }
`;

const SelectContainer = styled.div`
    width: 8rem;
`;

export const KoodistoPage: React.FC = () => {
    const { versio, koodistoUri } = useParams();
    const versioNumber = versio ? +versio : undefined;
    const navigate = useNavigate();
    const { formatMessage } = useIntl();
    const [lang] = useAtom(casMeLangAtom);
    const [koodisto, setKoodisto] = useState<PageKoodisto | undefined>();
    const [uploadCsvVisible, setUploadCsvVisible] = useState<boolean>(false);

    const incomingVersioOption: SelectOptionType = {
        label: formatMessage(
            {
                id: 'KOODISTOSIVU_VERSIO_DROPDOWN_LABEL',
                defaultMessage: 'Versio {versio}',
            },
            { versio: versio || '' }
        ),
        value: versio || '',
    };
    useEffect(() => {
        (async () => {
            if (koodistoUri && versioNumber) {
                const koodistoData = await fetchPageKoodisto(koodistoUri, versioNumber);
                setKoodisto(koodistoData);
            }
        })();
    }, [koodistoUri, versioNumber]);

    if (!koodisto) {
        return <Loading />;
    }
    const koodistonMetadata = translateMetadata({ metadata: koodisto.metadata, lang });
    const versioOptions = koodisto.koodiVersio.map((a) => {
        return {
            label: formatMessage(
                {
                    id: 'KOODISTOSIVU_VERSIO_DROPDOWN_LABEL',
                    defaultMessage: 'Versio {versio}',
                },
                { versio: a }
            ),
            value: a.toString(),
        };
    });
    return (
        <>
            <KoodistoPathContainer>
                <div>
                    <Link to={'/'}>
                        <IconWrapper icon="codicon:home" />
                        <FormattedMessage id={'KOODISTOPALVELU_OTSIKKO'} defaultMessage={'Koodistopalvelu'} />
                    </Link>
                    <span>&gt;</span>
                </div>
                <div>
                    <p>{koodistonMetadata?.nimi}</p>
                </div>
            </KoodistoPathContainer>
            <MainHeaderContainer>
                <HeadingDivider>
                    <h1>{koodistonMetadata?.nimi}</h1>
                    <SelectContainer>
                        <Select
                            onChange={(value) => {
                                if ((value as SelectOptionType).value !== incomingVersioOption.value) {
                                    navigate(`/koodisto/${koodisto.koodistoUri}/${(value as SelectOptionType).value}`);
                                }
                            }}
                            placeholder={formatMessage({
                                id: 'KOODISTOVERSIO_DROPDOWN',
                                defaultMessage: 'Koodistoversio',
                            })}
                            value={incomingVersioOption}
                            options={versioOptions}
                        />
                    </SelectContainer>
                </HeadingDivider>
                <MainHeaderButtonsContainer>
                    <Button variant={'outlined'}>
                        <FormattedMessage
                            id={'KOODISTOSIVU_MUOKKAA_KOODISTOA_BUTTON'}
                            defaultMessage={'Muokkaa koodistoa'}
                        />
                    </Button>
                    <Button variant={'outlined'} onClick={() => setUploadCsvVisible(true)} name={`${koodistoUri}-csv`}>
                        <FormattedMessage
                            id={'KOODISTOSIVU_TUO_VIE_KOODISTO_BUTTON'}
                            defaultMessage={'Lataa / tuo koodisto'}
                        />
                    </Button>
                </MainHeaderButtonsContainer>
            </MainHeaderContainer>
            <MainContainer>
                <InfoFields koodisto={koodisto} />
                <KoodistoPageAccordion koodisto={koodisto} />
            </MainContainer>
            {uploadCsvVisible && koodistoUri && (
                <CSVFunctionModal
                    koodistoUri={koodistoUri}
                    koodistoVersio={versioNumber}
                    closeUploader={() => setUploadCsvVisible(false)}
                />
            )}
        </>
    );
};
