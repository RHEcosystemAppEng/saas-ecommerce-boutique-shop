import React, {useEffect} from 'react';
import axios from "../../axios-middleware"
import {
    Brand,
    Button,
    Masthead,
    MastheadBrand,
    MastheadMain,
    Page,
    PageSection,
    PageSectionTypes,
    PageSectionVariants,
    Panel,
    PanelMain,
    Text,
    TextContent,
    Wizard,
    WizardContextConsumer,
    WizardFooter
} from '@patternfly/react-core';
import readHatLogo from "../../images/Logo-Red_Hat.png";
import {ServiceDetailsForm} from "../ServiceDetailsForm";
import {ServiceSummary} from "../ServiceSummary";
import {useNavigate} from "react-router-dom";
import {clearLocalStorage} from "../../Utils/Helper";

export const UpdateResources = () => {
    const navigate = useNavigate();
    const [isPrimaryLoading, setIsPrimaryLoading] = React.useState(false);
    const [isBtnDisabled, setIsBtnDisabled] = React.useState(false);
    const [resourceData, setResourceData] = React.useState([]);

    useEffect(() => {
        getResourceData()
    }, [])

    const getResourceData = () => {
        axios
            .get("/get-current-request/"+localStorage.getItem("tenantKey"))
            .then((res) => {
                setResourceData(res.data)
                localStorage.setItem("peakConcurrentShoppers", res.data.peakConcurrentShoppers);
                localStorage.setItem("avgConcurrentShoppers", res.data.avgConcurrentShoppers);
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }
    const validateAndSubmitData = k => {
        setIsBtnDisabled(true)
        setIsPrimaryLoading(!isPrimaryLoading)
        const formData = {
            "tenantName": localStorage.getItem("tenantName") || "",
            "avgConcurrentShoppers": localStorage.getItem("avgConcurrentShoppers") || null,
            "peakConcurrentShoppers": localStorage.getItem("peakConcurrentShoppers") || null,
        }

        axios
            .post("/resource-request", formData)
            .then((res) => {
                clearLocalStorage()
                localStorage.setItem("loggedInUserName", res.data.loggedInUserName)
                navigate("/dashboard/"+res.data.id)
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    };

    const Header = (
        <Masthead id="basic">
            <MastheadMain>
                <MastheadBrand>
                    <Brand src={readHatLogo} alt="Red Hat logo">
                        <source srcSet={readHatLogo} width={"150px"}/>
                    </Brand>
                </MastheadBrand>
            </MastheadMain>
        </Masthead>
    );
    const pageId = 'main-content-page-layout-default-nav';

    const steps = [
        {
            id: 1,
            name: 'Service Details',
            component: <ServiceDetailsForm/>
        },
        {
            id: 2,
            name: 'Summary',
            component: <ServiceSummary/>,
            nextButtonText: 'Finish'
        }
    ];

    const CustomFooter = (
        <WizardFooter>
            <WizardContextConsumer>
                {({activeStep, goToStepByName, goToStepById, onNext, onBack, onClose}) => {

                    if (activeStep.name !== 'Summary') {
                        return (
                            <>
                                <Button variant="primary" type="submit" onClick={onNext}>
                                    Next
                                </Button>
                                <Button
                                    variant="secondary"
                                    onClick={onBack}
                                    className={activeStep.name === 'Business Information' ? 'pf-m-disabled' : ''}
                                >
                                    Back
                                </Button>
                                <a href="/login">
                                    <Button variant="link">
                                        Cancel
                                    </Button>
                                </a>

                            </>
                        );
                    }

                    const primaryLoadingProps = {};
                    primaryLoadingProps.spinnerAriaValueText = "Loading";
                    primaryLoadingProps.spinnerAriaLabelledBy = "primary-loading-button";
                    primaryLoadingProps.isLoading = isPrimaryLoading;
                    // Final step buttons
                    return (
                        <>
                            <Button
                                variant="primary"
                                id="primary-loading-button"
                                onClick={() => validateAndSubmitData(onNext)}
                                isDisabled={isBtnDisabled}
                                {...primaryLoadingProps}
                            >
                                {isPrimaryLoading ? "Loading..." : "Confirm"}
                            </Button>
                            <Button variant="link"
                                    isDisabled={isBtnDisabled}
                                    onClick={() => goToStepByName('Business Information')}>Go to
                                Beginning</Button>
                        </>
                    );
                }}
            </WizardContextConsumer>
        </WizardFooter>
    );

    return (
        <React.Fragment>

            <Panel>
                <PanelMain>
                    <Page
                        header={Header}
                        mainContainerId={pageId}
                    >
                        <PageSection variant={PageSectionVariants.light}>
                            <TextContent>
                                <Text component="h1">Update the tenant resource limits</Text>
                                <Text component="p">Provide expected number of shoppers. Required resources will be adjusted accordingly.</Text>
                            </TextContent>
                        </PageSection>
                        <PageSection hasShadowTop={true} type={PageSectionTypes.wizard} isFilled={true}
                                     variant={PageSectionVariants.light}>
                            <Wizard
                                steps={steps}
                                hideClose={true}
                                footer={CustomFooter}
                            />
                        </PageSection>
                    </Page>
                </PanelMain>
            </Panel>
        </React.Fragment>
    );
}