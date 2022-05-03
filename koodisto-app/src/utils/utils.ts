import { ApiDate, Kieli, Metadata } from '../types/types';
import moment from 'moment';

export const translateMetadata = (metadata: Metadata[], lang: Kieli): Metadata | undefined =>
    metadata.find((a) => a.kieli === lang) || metadata.find((a) => a.kieli === 'FI');

export const parseApiDate = (a: ApiDate): Date => {
    return !!a && moment(a).toDate();
};
