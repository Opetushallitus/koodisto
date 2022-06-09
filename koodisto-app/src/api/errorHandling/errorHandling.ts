import Axios, { AxiosError, AxiosResponse } from 'axios';
import { danger, warning } from '../../components/Notification/Notification';

const handleError = <T>(error: AxiosError<T> | unknown) => {
    if (Axios.isAxiosError(error)) {
        const axiosError = error;
        if (axiosError.response) {
            console.error('axiosError', axiosError.response);
            if (axiosError.response.data) {
                warning({ title: `client.error.${axiosError.response.status}`, message: axiosError.response.data });
            } else {
                danger({ message: `server.error.${axiosError.response.status}` });
            }
        }
    } else {
        danger({ message: error as string });
        console.error(error);
    }
};
export const errorHandlingWrapper = async <A = never, B = AxiosResponse<A>>(
    workhorse: () => Promise<B>
): Promise<B | undefined> => {
    return workhorse().catch((e) => {
        handleError(e);
        return Promise.resolve(undefined);
    });
};
export const useErrorHandlingWrapper = <T>(workhorse: () => { data: T | undefined; loading: boolean }) => {
    try {
        return workhorse();
    } catch (error) {
        handleError(error);
        return { data: undefined, loading: false };
    }
};
