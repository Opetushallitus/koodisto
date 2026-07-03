import React from 'react';
import { FooterContainer, FooterLeftContainer, FooterRightContainer } from '../Containers';
import Button from 'virkailija-ui-components/Button';
import { FormattedMessage } from 'react-intl';
import { IconWrapper } from '../IconWapper';
import { useNavigate } from 'react-router-dom';
import { Tila } from '../../types';
import Popup from 'reactjs-popup';
import type { PopupActions, PopupProps } from 'reactjs-popup/dist/types';
import 'reactjs-popup/dist/index.css';

type Props = {
    returnPath: string;
    save: () => void;
    localisationPrefix: 'KOODI' | 'KOODISTO';
    versionDialog?: (close: () => void) => React.ReactNode;
    removeDialog: (close: () => void) => React.ReactNode;
    state: Tila;
    latest: boolean;
    locked?: boolean;
};

const contentStyle = { width: '300px' };
type PopupRenderChild = React.ReactNode | ((close: () => void, isOpen: boolean) => React.ReactNode);
type PopupPropsWithRenderChild = Omit<PopupProps, 'children'> & {
    children?: PopupRenderChild;
};
const PopupWithRenderChild = Popup as React.ForwardRefExoticComponent<
    PopupPropsWithRenderChild & React.RefAttributes<PopupActions>
>;

export const Footer: React.FC<Props> = ({
    returnPath,
    save,
    localisationPrefix,
    versionDialog,
    removeDialog,
    state,
    latest,
    locked,
}) => {
    const navigate = useNavigate();
    return (
        <FooterContainer>
            <FooterLeftContainer>
                {versionDialog && (
                    <PopupWithRenderChild
                        position="top left"
                        trigger={
                            <Button variant={'outlined'} name={`${localisationPrefix}_VERSIOI`} disabled={!latest}>
                                <FormattedMessage
                                    id={`${localisationPrefix}_VERSIOI`}
                                    defaultMessage={`Versioi ${localisationPrefix.toLowerCase()}`}
                                />
                            </Button>
                        }
                        {...{ contentStyle }}
                    >
                        {versionDialog}
                    </PopupWithRenderChild>
                )}
                <PopupWithRenderChild
                    position="top left"
                    trigger={
                        <Button variant={'outlined'} name={`${localisationPrefix}_POISTA`} disabled={locked}>
                            <IconWrapper icon={'ci:trash-full'} inline={true} height={'1.2rem'} />
                            <FormattedMessage
                                id={`${localisationPrefix}_POISTA`}
                                defaultMessage={`Poista ${localisationPrefix.toLowerCase()}`}
                            />
                        </Button>
                    }
                    {...{ contentStyle }}
                >
                    {removeDialog}
                </PopupWithRenderChild>
            </FooterLeftContainer>
            <FooterRightContainer>
                <Button
                    variant={'outlined'}
                    name={`${localisationPrefix}_PERUUTA`}
                    onClick={() => navigate(returnPath)}
                >
                    <FormattedMessage id={`${localisationPrefix}_PERUUTA`} defaultMessage={'Peruuta'} />
                </Button>
                <Button name={`${localisationPrefix}_TALLENNA`} onClick={save} disabled={state !== 'LUONNOS'}>
                    <FormattedMessage id={`${localisationPrefix}_TALLENNA`} defaultMessage={'Tallenna'} />
                </Button>
            </FooterRightContainer>
        </FooterContainer>
    );
};
