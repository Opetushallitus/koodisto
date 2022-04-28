import React, { useState } from 'react';
import {
    AccordionItem,
    Accordion as AC,
    AccordionItemHeading,
    AccordionItemButton,
    AccordionItemPanel,
} from 'react-accessible-accordion';
import IconWrapper from '../IconWapper/IconWrapper';
import { FormattedMessage } from 'react-intl';
import { UUID } from 'react-accessible-accordion/dist/types/components/ItemContext';
import styled from 'styled-components';

const SISALTYY_KOODISTOIHIN_ID = 0;
const SISALTAA_KOODISTOT_ID = 1;
const RINNASTUU_KOODISTOIHIN_ID = 2;

const ChevronIcon = styled(({ id, activeIds, ...rest }: { id: number; activeIds: UUID[] }) => {
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

const Accordion: React.FC = () => {
    const [activeAcIds, setActiveAcIds] = useState<UUID[]>([]);
    return (
        <AC onChange={setActiveAcIds}>
            <StyledAccordionItem uuid={SISALTYY_KOODISTOIHIN_ID}>
                <AccordionItemHeading>
                    <StyledAccordionItemButton>
                        <ChevronIcon activeIds={activeAcIds} id={SISALTYY_KOODISTOIHIN_ID} />
                        <FormattedMessage
                            id={'KOODISTOSIVU_OTSIKKO_SISALTYY_KOODISTOIHIN'}
                            defaultMessage={'Sisältyy koodistoihin (luku)'}
                        />
                    </StyledAccordionItemButton>
                </AccordionItemHeading>
                <AccordionItemPanel>
                    <p>
                        Exercitation in fugiat est ut ad ea cupidatat ut in cupidatat occaecat ut occaecat consequat est
                        minim minim esse tempor laborum consequat esse adipisicing eu reprehenderit enim.
                    </p>
                </AccordionItemPanel>
            </StyledAccordionItem>
            <StyledAccordionItem uuid={SISALTAA_KOODISTOT_ID}>
                <AccordionItemHeading>
                    <StyledAccordionItemButton>
                        <ChevronIcon activeIds={activeAcIds} id={SISALTAA_KOODISTOT_ID} />
                        <FormattedMessage
                            id={'KOODISTOSIVU_OTSIKKO_SISALTÄÄ_KOODISTOT'}
                            defaultMessage={'Sisältää koodistot (luku)'}
                        />
                    </StyledAccordionItemButton>
                </AccordionItemHeading>
                <AccordionItemPanel>
                    <p>sisältää</p>
                </AccordionItemPanel>
            </StyledAccordionItem>
            <StyledAccordionItem uuid={RINNASTUU_KOODISTOIHIN_ID}>
                <AccordionItemHeading>
                    <StyledAccordionItemButton>
                        <ChevronIcon activeIds={activeAcIds} id={RINNASTUU_KOODISTOIHIN_ID} />
                        <FormattedMessage
                            id={'KOODISTOSIVU_OTSIKKO_RINNASTUU_KOODISTOIHIN'}
                            defaultMessage={'Rinnastuu koodistoihin (luku)'}
                        />
                    </StyledAccordionItemButton>
                </AccordionItemHeading>
                <AccordionItemPanel>
                    <p>sisältää</p>
                </AccordionItemPanel>
            </StyledAccordionItem>
        </AC>
    );
};

export default Accordion;
