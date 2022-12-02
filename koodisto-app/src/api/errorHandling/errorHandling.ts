import Axios, { AxiosError, AxiosResponse } from 'axios';
import { danger, clientError } from '../../components/Notification/Notification';

type ErrorMessage = { message: string };
const isErrorMessage = (error: unknown): error is ErrorMessage => {
    return typeof (error as ErrorMessage).message === 'string';
};
const handleError = <T>(error: AxiosError<T> | unknown) => {
    if (Axios.isAxiosError(error)) {
        const axiosError = error;
        if (axiosError.response) {
            console.error('axiosError', axiosError.response);
            if (axiosError.response.data && axiosError.response.status < 500) {
                clientError(axiosError.response.status, axiosError.response.data);
            } else {
                danger({ message: `server.error.${axiosError.response.status}` });
            }
        }
    } else if (isErrorMessage(error)) {
        error.message !== 'canceled' && danger({ message: error.message });
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
