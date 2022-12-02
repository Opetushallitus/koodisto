import React, { ReactNode } from 'react';
import 'react-notifications-component/dist/theme.css';
import { iNotification, ReactNotifications, Store } from 'react-notifications-component';
import {
    NOTIFICATION_CONTAINER,
    NOTIFICATION_INSERTION,
    NOTIFICATION_TYPE,
} from 'react-notifications-component/dist/src/typings';
import { FormattedMessage } from 'react-intl';

const DEFAULT_TIMEOUT = 5000;
const MESSAGE_DEFAULTS = {
    insert: 'top' as NOTIFICATION_INSERTION,
    container: 'top-right' as NOTIFICATION_CONTAINER,
    animationIn: ['animate__animated', 'animate__fadeIn'],
    animationOut: ['animate__animated', 'animate__fadeOut'],
};

type NotificationProps = {
    id?: string;
    title?: ReactNode | string;
    message: ReactNode | string;
    values?: Record<string, string | number>;
    timeOut?: number;
};
const messageInputs = ({ id, title, message, values, timeOut }: NotificationProps): iNotification => ({
    ...MESSAGE_DEFAULTS,
    title:
        typeof title === 'string' ? (
            <FormattedMessage id={`${id || title}-title`} defaultMessage={title} values={values} />
        ) : (
            title
        ),
    message:
        typeof message === 'string' ? (
            <FormattedMessage id={`${id || title}-message`} defaultMessage={message} values={values} />
        ) : (
            message
        ),
    dismiss: {
        duration: timeOut || DEFAULT_TIMEOUT,
        pauseOnHover: true,
        showIcon: true,
    },
});
export const Notification = () => {
    return <ReactNotifications />;
};

const warning = (props: NotificationProps) => {
    Store.addNotification({
        ...messageInputs(props),
        type: 'warning' as NOTIFICATION_TYPE,
    });
};

const clientStatusToMessage: Record<number, string> = {
    400: 'Selain virhe {status}, ota yhteyttä ylläpitoon.',
    404: 'Tietue ei löytynyt palvelimelta.',
};
export const clientError = (status: number, data: string) =>
    warning({
        id: `client.error.${status}`,
        title: clientStatusToMessage[status] || `Selain virhe {status}, ota yhteyttä ylläpitoon.`,
        message: data,
        values: { status },
    });

export const success = (props: NotificationProps) => {
    Store.addNotification({
        ...messageInputs(props),
        type: 'success' as NOTIFICATION_TYPE,
    });
};
export const info = (props: NotificationProps) => {
    Store.addNotification({
        ...messageInputs(props),
        type: 'info' as NOTIFICATION_TYPE,
    });
};
export const danger = (props: NotificationProps) => {
    Store.addNotification({
        ...messageInputs(props),
        type: 'danger' as NOTIFICATION_TYPE,
    });
};
