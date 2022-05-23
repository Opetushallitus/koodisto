import axios from 'axios';
import { errorHandlingWrapper } from './errorHandling/errorHandling';
import { OrganisaatioNimi } from '../types/types';

type Organisaatio = {
    nimi: OrganisaatioNimi;
};

export const fetchOrganisaatioNimi = async (oid: string): Promise<OrganisaatioNimi | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.get<Organisaatio>(`/organisaatio-service/api/${oid}`);
        return data.nimi;
    });
};
