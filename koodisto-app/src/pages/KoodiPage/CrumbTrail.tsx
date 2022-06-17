import React from 'react';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import { KoodistoPathContainer } from '../../components/KoodistoPathContainer';
import type { PageKoodi } from '../../types';

export const CrumbTrail: React.FC<PageKoodi> = ({ koodi, koodisto }: PageKoodi) => {
    const [lang] = useAtom(casMeLangAtom);
    const trail = [
        {
            path: `/koodisto/view/${koodisto.koodistoUri}/${koodisto.versio}`,
            label: translateMetadata({ metadata: koodisto.metadata, lang })?.nimi || koodisto.koodistoUri,
        },
        {
            label: translateMetadata({ metadata: koodi.metadata, lang })?.nimi || koodi.koodiUri,
        },
    ];
    return <KoodistoPathContainer trail={trail} />;
};
