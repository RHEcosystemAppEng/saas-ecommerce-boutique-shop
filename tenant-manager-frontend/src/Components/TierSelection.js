import React, {useContext, useState} from 'react';
import {Button, Card, CardBody, CardFooter, CardTitle, Flex, FlexItem, WizardContextConsumer} from '@patternfly/react-core';

const styles = {
    cardContainerStyle: {
        width: "33%",
        maxWidth: "300px",
        boxShadow: "0 0 2px 1px #C9190B"
    },
    cardStyle: {
        height: "100%"
    },
    footer: {
        display: "flex",
        justifyContent: "center"
    }
}

export const TierSelection = () => {
    const { onNext: onWizardNext } = useContext(WizardContextConsumer);

    const [selectedTier, setSelectedTier] = useState(() => {
        return localStorage.getItem("tier") || null;
    });
    const [freeTier, setFreeTier] = useState(() => {
        return localStorage.getItem("freeTier") || false;
    });
    const [silverTier, setSilverTier] = useState(() => {
        return localStorage.getItem("silverTier") || false;
    });
    const [goldTier, setGoldTier] = useState(() => {
        return localStorage.getItem("goldTier") || false;
    });
    const [platinumTier, setPlatinumTier] = useState(() => {
        return localStorage.getItem("platinumTier") || false;
    });

    const onSelectTier = (tier) => {
        setFreeTier(false);localStorage.removeItem("freeTier");
        setSilverTier(false);localStorage.removeItem("silverTier");
        setGoldTier(false);localStorage.removeItem("goldTier");
        setPlatinumTier(false);localStorage.removeItem("platinumTier");
        switch (tier) {
            case "Free" :
                setFreeTier(true);
                localStorage.setItem("freeTier", true);
                break;
            case "Silver" :
                setSilverTier(true);
                localStorage.setItem("silverTier", true);
                break;
            case "Gold" :
                setGoldTier(true);
                localStorage.setItem("goldTier", true);
                break;
            case "Platinum" :
                setPlatinumTier(true);
                localStorage.setItem("platinumTier", true);
                break;
        }
        setSelectedTier(tier);
        localStorage.setItem("tier", tier);
        onWizardNext();
    }

    return (
        <Flex style={{justifyContent: "center"}}>
            <FlexItem style={styles.cardContainerStyle}
                      alignSelf={{default: 'alignSelfStretch'}}>
                <Card style={styles.cardStyle} isSelectableRaised={true} isSelected={freeTier}
                      onSelectableInputChange={() => onSelectTier("Free")}>
                    <CardTitle component="h4">Free</CardTitle>
                    <CardBody>Shop owners - try out the Boutique Shop e-commerce service FREE for 30 days. <br/><br/>
                        During this period, you can choose your custom business URL, and have up to 10 concurrent web
                        shoppers.
                    </CardBody>
                    <CardFooter style={styles.footer}>
                        <Button variant="primary" isLarge onClick={() => onSelectTier("Free")}>Subscribe</Button>
                    </CardFooter>
                </Card>
            </FlexItem>
            <FlexItem style={styles.cardContainerStyle}
                      alignSelf={{default: 'alignSelfStretch'}}>
                <Card style={styles.cardStyle} isSelectableRaised={true} isSelected={silverTier}
                      onSelectableInputChange={() => onSelectTier("Silver")}>
                    <CardTitle component="h4">Silver</CardTitle>
                    <CardBody>Silver tier is an affordable option that provides you fundamental e-commerce services to
                        get started. <br/><br/> Simple payment model - pay by the maximum number of concurrent shoppers.
                        <br/><br/> We ensure reliable web response times, and an up-time performance to ensure a smooth
                        shopping experience.
                    </CardBody>
                    <CardFooter style={styles.footer}>
                        <Button variant="primary" isLarge onClick={() => onSelectTier("Silver")}>Subscribe</Button>
                    </CardFooter>
                </Card>
            </FlexItem>
            <FlexItem style={styles.cardContainerStyle}
                      alignSelf={{default: 'alignSelfStretch'}}>
                <Card style={styles.cardStyle} isSelectableRaised={true} isSelected={goldTier}
                      onSelectableInputChange={() => onSelectTier("Gold")}>
                    <CardTitle component="h4">Gold</CardTitle>
                    <CardBody>
                        In addition to the benefits for Silver Tier, Gold Tier subscribers can customize your storefront
                        to your liking - create a look & feel that your clientele will identify with. <br/><br/>
                        Additional customization is available. <br/><br/>Concurrent user pricing starts at $20/month up
                        to 500 concurrent users.
                    </CardBody>
                    <CardFooter style={styles.footer}>
                        <Button variant="primary" isLarge onClick={() => onSelectTier("Gold")}>Subscribe</Button>
                    </CardFooter>
                </Card>
            </FlexItem>
            <FlexItem style={styles.cardContainerStyle}
                      alignSelf={{default: 'alignSelfStretch'}}>
                <Card style={styles.cardStyle} isSelectableRaised={true} isSelected={platinumTier}
                      onSelectableInputChange={() => onSelectTier("Platinum")}>
                    <CardTitle component="h4">Platinum</CardTitle>
                    <CardBody>
                        In addition to Gold tier features, our Platinum Tier will scale automatically to your business -
                        even during peak holiday shopping periods. <br/><br/>This is recommended for multi-location
                        businesses, firms with an established web business, or concurrent user requirements that
                        exceed 500 users.
                    </CardBody>
                    <CardFooter style={styles.footer}>
                        <Button variant="primary" isLarge onClick={() => onSelectTier("Platinum")}>Subscribe</Button>
                    </CardFooter>
                </Card>
            </FlexItem>
        </Flex>
    )
}