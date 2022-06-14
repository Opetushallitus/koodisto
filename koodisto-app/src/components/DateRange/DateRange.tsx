import React from 'react';
import { FormattedDate } from 'react-intl';
import { ApiDate } from '../../types';

type Props = {
    from: ApiDate;
    to?: ApiDate;
};

export const DateRange: React.FC<Props> = ({ from, to }: Props) => (
    <>
        {[<FormattedDate key="from" value={from} />, to && ' - ', to && <FormattedDate key="to" value={to} />].filter(
            Boolean
        )}
    </>
);
