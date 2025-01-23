import React from 'react';
import { FormattedDate } from 'react-intl';

type Props = {
    from: Date;
    to?: Date;
};

export const DateRange: React.FC<Props> = ({ from, to }: Props) => (
    <>
        {[<FormattedDate key="from" value={from} />, to && ' - ', to && <FormattedDate key="to" value={to} />].filter(
            Boolean
        )}
    </>
);
