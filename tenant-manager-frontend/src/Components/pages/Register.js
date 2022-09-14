import React from 'react';
import axios from "../../axios-middleware"
import {
    Brand,
    Masthead,
    MastheadBrand,
    MastheadMain,
    Page,
    PageSection,
    PageSectionTypes,
    PageSectionVariants,
    Text,
    TextContent,
    Wizard,
    WizardFooter,
    WizardContextConsumer,
    Button,
    Panel, PanelMain
} from '@patternfly/react-core';
import {BusinessInfoForm} from "../BusinessInfoForm";
import {TierSelection} from "../TierSelection";
import {ServiceDetailsForm} from '../ServiceDetailsForm';
import {ServiceSummary} from '../ServiceSummary';
import readHatLogo from '../../images/Logo-Red_Hat.png';
import {useNavigate} from "react-router-dom"
import {clearLocalStorage} from "../../Utils/Helper";

export const Register = (props) => {
    const navigate = useNavigate();
    const [isPrimaryLoading, setIsPrimaryLoading] = React.useState(false);
    const [isBtnDisabled, setIsBtnDisabled] = React.useState(false);

    const validateAndSubmitData = k => {
        // console.log('JUDE ADDED:::' + JSON.stringify(props))

        setIsBtnDisabled(true)
        setIsPrimaryLoading(!isPrimaryLoading)
        const formData = {
            "email": localStorage.getItem("email") || "",
            "password": localStorage.getItem("password") || "",
            "tenantName": localStorage.getItem("tenantName") || "",
            "orgName": localStorage.getItem("orgName") || "",
            "address": localStorage.getItem("orgAddress") || "",
            "phone": localStorage.getItem("phone") || "",
            "contactPerson": localStorage.getItem("contactName") || "",
            "serviceLevel": localStorage.getItem("tier") || "",
            "avgConcurrentShoppers": localStorage.getItem("avgConcurrentShoppers") || null,
            "peakConcurrentShoppers": localStorage.getItem("peakConcurrentShoppers") || null,
            "fromTime": localStorage.getItem("fromTime") || null,
            "toTime": localStorage.getItem("toTime") || null
        }

        axios
            .post("/signup", formData)
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
            name: 'Business Information',
            component: <BusinessInfoForm/>
        },
        {
            id: 2,
            name: 'Subscription Configuration',
            component: <TierSelection/>
        },
        {
            id: 3,
            name: 'Service Details',
            component: <ServiceDetailsForm/>
        },
        {
            id: 4,
            name: 'Summary',
            component: <ServiceSummary/>,
            nextButtonText: 'Finish'
        }
    ];

    const CustomFooter = (
        <WizardFooter>
            <WizardContextConsumer>
                {({activeStep, goToStepByName, goToStepById, onNext, onBack, onClose}) => {

                    if (activeStep.name === 'Subscription Configuration') {
                        return (
                            <>
                                <Button
                                    variant="secondary"
                                    onClick={onBack}
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
                                <Text component="h1">Register for a new tenant account</Text>
                                <Text component="p">Software vendors provide SaaS-style software applications to
                                    meet any
                                    number of business needs
                                    <br/> from basic business applications to complex enterprise resource planning
                                    (ERP)
                                    suites.</Text>
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