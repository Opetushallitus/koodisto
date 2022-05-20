import axios from 'axios';
import { errorHandlingWrapper } from './errorHandling/errorHandling';

type Organisaatio = {
    nimi: {
        fi: string;
        sv: string;
        en: string;
    };
};

export const fetchOrganisaatio = async (oid: string): Promise<Organisaatio | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.get<Organisaatio>(`/organisaatio-service/rest/organisaatio/${oid}`);
        return data;
    });
};
