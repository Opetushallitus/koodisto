import React from 'react';
import { FormattedMessage } from 'react-intl';

/*
    Purpose for this component is to help message extractions process to 
    catch dynamically generated message keys along with default translations.
    This component should not be in use anywhere but is arguably important 
    nevertheless.
 */
export const TranslationHelper = () => (
    <>
        <FormattedMessage id="TILA_PASSIIVINEN" defaultMessage={'Passiivinen'} />
        <FormattedMessage id="TILA_LUONNOS" defaultMessage={'Luonnos'} />
        <FormattedMessage id="TILA_HYVAKSYTTY" defaultMessage={'Hyväksytty'} />
        <FormattedMessage id={'KOODISTO_RYHMA_FI'} defaultMessage={'FI'} />
        <FormattedMessage id={'KOODISTO_RYHMA_SV'} defaultMessage={'SV'} />
        <FormattedMessage id={'KOODISTO_RYHMA_EN'} defaultMessage={'EN'} />
    </>
);
