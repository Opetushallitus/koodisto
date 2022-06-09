import * as React from 'react';
import { KoodistoRyhma } from '../../types';
import { deleteKoodistoRyhma } from '../../api/koodisto';
import { FormattedMessage } from 'react-intl';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { IconWrapper } from '../../components/IconWapper';

export const EmptyKoodistoRyhmaList: React.FC<{
    emptyKoodistoRyhma: KoodistoRyhma[];
    setEmptyKoodistoRyhma: (a: KoodistoRyhma[]) => void;
}> = ({ emptyKoodistoRyhma, setEmptyKoodistoRyhma }) => {
    const onDelete = async (uri: string) => {
        const data = await deleteKoodistoRyhma(uri);
        data && setEmptyKoodistoRyhma(data);
    };
    return (
        <>
            <h2>
                <FormattedMessage
                    id={'KOODISTO_RYHMA_TYHJAT_TITLE'}
                    defaultMessage={'Tyhjät koodistoryhmät ({emptyRyhmaCount})'}
                    values={{ emptyRyhmaCount: emptyKoodistoRyhma.length }}
                />
            </h2>
            {emptyKoodistoRyhma.map((a) => (
                <div key={a.koodistoRyhmaUri}>
                    {a.koodistoRyhmaUri}
                    <Button variant={'text'} name={`POISTA_KOODISTORYHMA-${a.koodistoRyhmaUri}`}>
                        <IconWrapper
                            onClick={() => onDelete(a.koodistoRyhmaUri)}
                            icon="ci:trash-full"
                            color={'gray'}
                            height={'1.5rem'}
                        />
                    </Button>
                </div>
            ))}
        </>
    );
};
