import React, { useEffect, useState } from 'react';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import { CrumbTrail } from '../../components/CrumbTrail';
import type { Koodi, PageKoodisto } from '../../types';
import { fetchPageKoodisto } from '../../api/koodisto';
import Spin from '@opetushallitus/virkailija-ui-components/Spin';

export const KoodiCrumbTrail: React.FC<{ koodi: Koodi; koodistoUriParam?: string }> = ({ koodi, koodistoUriParam }) => {
    const [lang] = useAtom(casMeLangAtom);
    const [koodisto, setKoodisto] = useState<PageKoodisto | undefined>(undefined);
    const koodistoUri = koodi?.koodisto?.koodistoUri || koodistoUriParam;
    const versio = koodi?.koodisto?.versio;
    useEffect(() => {
        (async () => {
            if (koodistoUri) {
                const data = await fetchPageKoodisto({ koodistoUri, versio, lang });
                setKoodisto(data);
            }
        })();
    }, [koodistoUri, versio, lang]);
    const trail = [
        ...((koodisto && [
            {
                path: `/koodisto/view/${koodisto.koodistoUri}/${koodisto.versio}`,
                label: translateMetadata({ metadata: koodisto.metadata, lang })?.nimi || koodisto.koodistoUri,
            },
        ]) || [{ path: undefined, label: <Spin size={'small'} /> }]),
        { key: koodi.koodiUri, label: translateMetadata({ metadata: koodi.metadata, lang })?.nimi || koodi.koodiUri },
    ];
    return <CrumbTrail trail={trail} />;
};
