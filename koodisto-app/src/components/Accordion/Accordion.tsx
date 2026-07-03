import React, { ReactNode, useState } from 'react';
import {
    AccordionItem,
    Accordion as AC,
    AccordionItemHeading,
    AccordionItemButton,
    AccordionItemPanel,
} from 'react-accessible-accordion';
import { IconWrapper } from '../IconWapper';
import styled from 'styled-components';

type AccordionId = NonNullable<React.ComponentProps<typeof AC>['preExpanded']>[number];

const ChevronIcon = styled(({ id, activeIds, ...rest }: { id: AccordionId; activeIds: AccordionId[] }) => {
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
const AccordionHeader = styled.div`
    flex: 1;
    justify-content: space-between;
    display: inline-flex;
    flex-direction: row;
    align-items: baseline;
    margin: 1rem;
`;
type AccordionDataItem = {
    id: AccordionId;
    localizedHeadingTitle: ReactNode;
    panelComponent: ReactNode;
};

type AccordionProps = {
    data: AccordionDataItem[];
    open?: AccordionId[];
};

export const Accordion: React.FC<AccordionProps> = ({ data, open = [] }) => {
    const [activeAcIds, setActiveAcIds] = useState<AccordionId[]>([...open]);
    return (
        <AC onChange={(ids) => setActiveAcIds(ids)} allowZeroExpanded allowMultipleExpanded preExpanded={activeAcIds}>
            {data.map((item) => {
                return (
                    <StyledAccordionItem key={item.id} uuid={item.id}>
                        <AccordionItemHeading>
                            <StyledAccordionItemButton>
                                <ChevronIcon activeIds={activeAcIds} id={item.id} />
                                <AccordionHeader>{item.localizedHeadingTitle}</AccordionHeader>
                            </StyledAccordionItemButton>
                        </AccordionItemHeading>
                        <AccordionItemPanel>{item.panelComponent}</AccordionItemPanel>
                    </StyledAccordionItem>
                );
            })}
        </AC>
    );
};
