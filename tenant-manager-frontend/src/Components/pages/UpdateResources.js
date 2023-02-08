import React from 'react';
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
import {useNavigate, useParams} from "react-router-dom";
import {clearLocalStorage} from "../../Utils/Helper";

export const UpdateResources = () => {
    const navigate = useNavigate();
    const [isPrimaryLoading, setIsPrimaryLoading] = React.useState(false);
    const [isBtnDisabled, setIsBtnDisabled] = React.useState(false);
    const [isEdited, setIsEdited] = React.useState(false);
    const { key } = useParams();


    const validateAndSubmitData = () => {
        setIsBtnDisabled(true)
        setIsPrimaryLoading(!isPrimaryLoading)
        const formData = {
            "tenantKey": localStorage.getItem("tenantKey") || "",
            "tier": localStorage.getItem("tier") || "",
            "hostName": localStorage.getItem("hostName") || "",
            "avgConcurrentShoppers": localStorage.getItem("avgConcurrentShoppers") || null,
            "peakConcurrentShoppers": localStorage.getItem("peakConcurrentShoppers") || null,
            "fromTime": localStorage.getItem("fromTime") || "",
            "toTime": localStorage.getItem("toTime") || ""
        }

        axios
            .post("/request/resource", formData)
            .then((res) => {
                clearLocalStorage()
                localStorage.setItem("loggedInUserName", res.data.loggedInUserName)
                localStorage.setItem("tenantKey", res.data.key)
                navigate("/dashboard/"+res.data.key)
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
            component: <ServiceDetailsForm isEdited={isEdited} markDataChanged={setIsEdited} disableHostName={true}/>
        },
        {
            id: 2,
            name: 'Summary',
            component: <ServiceSummary/>,
            nextButtonText: 'Finish'
        }
    ];

    const nextHandler = (onNext) => {
        setIsEdited(true)
        return onNext;
    }

    const cancelHandler = () => {
        localStorage.removeItem("hostName")
        localStorage.removeItem("avgConcurrentShoppers")
        localStorage.removeItem("peakConcurrentShoppers")
        localStorage.removeItem("tier")
        navigate("/dashboard/"+key)
    }

    const CustomFooter = (
        <WizardFooter>
            <WizardContextConsumer>
                {({activeStep, goToStepByName, goToStepById, onNext, onBack, onClose}) => {

                    if (activeStep.name !== 'Summary') {
                        return (
                            <>
                                <Button variant="primary" type="submit" onClick={nextHandler(onNext)}>
                                    Next
                                </Button>
                                <a href={"/dashboard/"+key}>
                                    <Button variant="link" onClick={cancelHandler}>
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
                                    onClick={onBack}>Back</Button>
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
                        style={{height:"100vh"}}
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