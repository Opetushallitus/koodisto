import React from 'react';
import { useAtom } from 'jotai';
import { casMeAtom } from '../../api/kayttooikeus';

const VIRKAILIJA_RAAMIT_PROD_URL = '/virkailija-raamit/apply-raamit.js';
const VIRKAILIJA_RAAMIT_DEV_URL = '/koodisto-app/dev-raamit.js';
const SCRIPT_ELEMENT_ID = 'virkailija-raamit-script';

const Raamit: React.FC = ({ children }) => {
    const [health] = useAtom(casMeAtom);
    if (process.env.NODE_ENV === 'development' && !document.getElementById(SCRIPT_ELEMENT_ID)) {
        console.log(health);
        const scriptElement = document.createElement('script');
        scriptElement.src = VIRKAILIJA_RAAMIT_DEV_URL;
        scriptElement.id = SCRIPT_ELEMENT_ID;
        document.body.appendChild(scriptElement);
    } else if (!document.getElementById(SCRIPT_ELEMENT_ID)) {
        const scriptElement = document.createElement('script');
        scriptElement.src = VIRKAILIJA_RAAMIT_PROD_URL;
        scriptElement.id = SCRIPT_ELEMENT_ID;
        document.body.appendChild(scriptElement);
    }
    return <>{children}</>;
};
export default Raamit;
