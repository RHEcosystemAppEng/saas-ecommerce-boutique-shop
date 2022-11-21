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
    const [premiumTier, setPremiumTier] = useState(() => {
        return localStorage.getItem("premiumTier") || false;
    });

    const onSelectTier = (tier) => {
        setFreeTier(false);localStorage.removeItem("freeTier");
        setSilverTier(false);localStorage.removeItem("silverTier");
        setGoldTier(false);localStorage.removeItem("goldTier");
        setPremiumTier(false);localStorage.removeItem("premiumTier");
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
            case "Premium" :
                setPremiumTier(true);
                localStorage.setItem("premiumTier", true);
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
                    <CardBody>Free Tier allows 5 concurrent shoppers, <br/><br/> there is no technical support. <br/><br/> 30 day free
                        trial. </CardBody>
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
                    <CardBody>Silver Tier, provide reliable service with technical support. <br/><br/> The price will be on the
                        usage, <br/><br/> $10/ month service fee to support every 100 concurrent online shoppers. </CardBody>
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
                        Gold Tier. provides the best shopping experience with quick page response time. <br/><br/> This tier is
                        provided with technical support, in addition tenant can customize the store front webpage
                        design. <br/><br/>The price will be $20/month for every 100 concurrent shoppers.
                    </CardBody>
                    <CardFooter style={styles.footer}>
                        <Button variant="primary" isLarge onClick={() => onSelectTier("Gold")}>Subscribe</Button>
                    </CardFooter>
                </Card>
            </FlexItem>
            <FlexItem style={styles.cardContainerStyle}
                      alignSelf={{default: 'alignSelfStretch'}}>
                <Card style={styles.cardStyle} isSelectableRaised={true} isSelected={premiumTier}
                      onSelectableInputChange={() => onSelectTier("Premium")}>
                    <CardTitle component="h4">Premium</CardTitle>
                    <CardBody>
                    Premium Tier - provider best security protection. Tenants can have own instance of application which completely isolated from others. <br/><br/>
                    The price will be $40/month subscription fee to support every 100 concurrent shoppers
                    </CardBody>
                    <CardFooter style={styles.footer}>
                        <Button variant="primary" isLarge onClick={() => onSelectTier("Premium")}>Subscribe</Button>
                    </CardFooter>
                </Card>
            </FlexItem>
        </Flex>
    )
}