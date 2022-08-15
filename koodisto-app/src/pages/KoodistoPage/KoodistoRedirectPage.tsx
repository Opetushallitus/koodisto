import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { useAtom } from 'jotai';
import { apiKoodistoListAtom, koodistoListAtom } from '../../api/koodisto';
import { Loading } from '../../components/Loading';

export const KoodistoRedirectPage: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { koodistoUri } = useParams();
    const [, refreshKoodistot] = useAtom(apiKoodistoListAtom);
    const [koodistot] = useAtom(koodistoListAtom);
    const [refreshed, setRefreshed] = useState<boolean>(false);

    useEffect(() => {
        if (refreshed) {
            const found = koodistot.find((koodisto) => koodisto.koodistoUri === koodistoUri);
            if (found) {
                navigate(`${location.pathname}/${found.versio}`);
            } else {
                navigate('/');
            }
        } else {
            refreshKoodistot();
            setRefreshed(true);
        }
    }, [koodistot, refreshKoodistot, refreshed, setRefreshed, koodistoUri, navigate, location]);
    return <Loading />;
};
