import React, { useEffect, useState } from 'react';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import { CrumbTrail } from '../../components/KoodistoPathContainer';
import type { Koodi, PageKoodisto } from '../../types';
import { fetchPageKoodisto } from '../../api/koodisto';
import Spin from '@opetushallitus/virkailija-ui-components/Spin';

export const KoodiCrumbTrail: React.FC<{ koodi: Koodi }> = ({ koodi }) => {
    const [lang] = useAtom(casMeLangAtom);
    const [koodisto, setKoodisto] = useState<PageKoodisto | undefined>(undefined);
    useEffect(() => {
        (async () => {
            const data = await fetchPageKoodisto({ koodistoUri: koodi.koodistoUri, lang });
            setKoodisto(data);
        })();
    }, [koodi.koodistoUri, lang]);
    const trail = [
        ...((koodisto && [
            {
                key: koodisto.koodistoUri,
                path: `/koodisto/view/${koodisto.koodistoUri}/${koodisto.versio}`,
                label: translateMetadata({ metadata: koodisto.metadata, lang })?.nimi || koodisto.koodistoUri,
            },
        ]) || [{ key: 'loading', path: undefined, label: <Spin size={'small'} /> }]),
        { key: koodi.koodiUri, label: translateMetadata({ metadata: koodi.metadata, lang })?.nimi || koodi.koodiUri },
    ];
    return <CrumbTrail trail={trail} />;
};
