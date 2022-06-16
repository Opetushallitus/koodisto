import React from 'react';
import { FormattedDate } from 'react-intl';

type Props = {
    at: Date;
    by?: string;
};

export const UpdatedAt: React.FC<Props> = ({ at, by }: Props) => (
    <>{[<FormattedDate key="at" value={at} />, by && ` (${by})`].filter(Boolean)}</>
);
