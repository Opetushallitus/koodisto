import { ApiDate, Kieli } from '../types/types';
import { Metadata } from '../api/koodisto';
import moment from 'moment';

export const translateMetadata = (a: Metadata[], lang: Kieli): string | undefined => {
    return (a.find((a) => a.kieli === lang) || a.find((a) => a.kieli === 'FI') || {}).nimi;
};

export const parseApiDate = (a: ApiDate): Date => {
    return !!a && moment(a).toDate();
};
