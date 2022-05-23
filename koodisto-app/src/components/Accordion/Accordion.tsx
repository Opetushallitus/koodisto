import React, { ReactNode, useState } from 'react';
import {
    AccordionItem,
    Accordion as AC,
    AccordionItemHeading,
    AccordionItemButton,
    AccordionItemPanel,
} from 'react-accessible-accordion';
import { IconWrapper } from '../IconWapper';
import { UUID } from 'react-accessible-accordion/dist/types/components/ItemContext';
import styled from 'styled-components';

const ChevronIcon = styled(({ id, activeIds, ...rest }: { id: number | string; activeIds: UUID[] }) => {
    return <IconWrapper icon={`el:chevron-${activeIds.includes(id) ? 'down' : 'right'}`} {...rest} />;
})`
    font-size: 1.2rem;
    padding: 0 2rem 0 1rem;
`;

const StyledAccordionItem = styled(AccordionItem)`
    border: 1px #cccccc solid;
    margin: 2rem 0 2rem 0;
`;

const StyledAccordionItemButton = styled(AccordionItemButton)`
    width: 100%;
    height: 40px;
    cursor: pointer;
    display: flex;
    align-items: center;
    background: #f5f5f5;
`;

type AccordionDataItem = {
    id: number | string;
    localizedHeadingTitle: string;
    panelComponent: ReactNode;
};

type AccordionProps = {
    data: AccordionDataItem[];
};

export const Accordion = (props: AccordionProps) => {
    const { data } = props;
    const [activeAcIds, setActiveAcIds] = useState<UUID[]>([]);
    return (
        <AC onChange={setActiveAcIds}>
            {data.map((item) => {
                return (
                    <StyledAccordionItem key={item.id} uuid={item.id}>
                        <AccordionItemHeading>
                            <StyledAccordionItemButton>
                                <ChevronIcon activeIds={activeAcIds} id={item.id} />
                                <span>{item.localizedHeadingTitle}</span>
                            </StyledAccordionItemButton>
                        </AccordionItemHeading>
                        <AccordionItemPanel>{item.panelComponent}</AccordionItemPanel>
                    </StyledAccordionItem>
                );
            })}
        </AC>
    );
};
