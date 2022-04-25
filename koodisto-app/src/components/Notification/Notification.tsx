import React from 'react';
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
const messageInputs = ({ title, message, timeOut }: NotificationProps): iNotification => ({
    ...MESSAGE_DEFAULTS,
    // @ts-ignore
    title: title ? <FormattedMessage id={title} defaultMessage={title} /> : undefined,
    message: message ? <FormattedMessage id={message} defaultMessage={message} /> : undefined,
    dismiss: {
        duration: timeOut || DEFAULT_TIMEOUT,
        pauseOnHover: true,
        showIcon: true,
    },
});
const Notification = () => {
    return <ReactNotifications />;
};
type NotificationProps = {
    title?: string;
    message: string;
    timeOut?: number;
};

export const warning = (props: NotificationProps) => {
    Store.addNotification({
        ...messageInputs(props),
        type: 'warning' as NOTIFICATION_TYPE,
    });
};
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

export default Notification;
