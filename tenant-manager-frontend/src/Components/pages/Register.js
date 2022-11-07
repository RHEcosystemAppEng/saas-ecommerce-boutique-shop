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
import {BusinessInfoForm} from "../BusinessInfoForm";
import {TierSelection} from "../TierSelection";
import {ServiceDetailsForm} from '../ServiceDetailsForm';
import {ServiceSummary} from '../ServiceSummary';
import readHatLogo from '../../images/Logo-Red_Hat.png';
import {useNavigate} from "react-router-dom"
import {clearLocalStorage} from "../../Utils/Helper";
import {ModalDialog} from "../ModalDialog";

export const Register = (props) => {
    const navigate = useNavigate();
    const [isPrimaryLoading, setIsPrimaryLoading] = React.useState(false);
    const [isBtnDisabled, setIsBtnDisabled] = React.useState(false);
    const [stepIdReached, setStepIdReached] = React.useState(1);
    const [isModalShowing, setIsModalShowing] = React.useState(false);
    const [modalData, setModalData] = React.useState();
    const [isValidBusinessForm, setIsValidBusinessForm] = React.useState({
        tenantName: true,
        orgName: true,
        orgAddress: true,
        phone: true,
        contactName: true
    });

    useEffect(() => {
        clearLocalStorage()
    }, [])

    const validateAndSubmitData = k => {

        setIsBtnDisabled(true)
        setIsPrimaryLoading(!isPrimaryLoading)
        const formData = {
            "email": localStorage.getItem("email") || "",
            "password": localStorage.getItem("password") || "",
            "tenantName": localStorage.getItem("tenantName") || "",
            "orgName": localStorage.getItem("orgName") || "",
            "address": localStorage.getItem("orgAddress") || "",
            "phone": localStorage.getItem("phone") || "",
            "contactName": localStorage.getItem("contactName") || "",
            "tier": localStorage.getItem("tier") || "",
            "hostName": localStorage.getItem("hostName") || "",
            "avgConcurrentShoppers": localStorage.getItem("avgConcurrentShoppers") || null,
            "peakConcurrentShoppers": localStorage.getItem("peakConcurrentShoppers") || null,
            "fromTime": localStorage.getItem("fromTime") || null,
            "toTime": localStorage.getItem("toTime") || null
        }

        axios
            .post("/tenant/signup", formData)
            .then((res) => {
                clearLocalStorage()
                localStorage.setItem("loggedInUserName", res.data.loggedInUserName)
                localStorage.setItem("tenantKey", res.data.key)
                navigate("/dashboard/" + res.data.key)
            })
            .catch((err) => {
                setIsModalShowing(true)
                setModalData({
                    title: "Server Connection Failed",
                    body: err.message
                })
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
            component: <BusinessInfoForm isValid={isValidBusinessForm}/>
        },
        {
            id: 2,
            name: 'Subscription Configuration',
            component: <TierSelection/>,
            canJumpTo: stepIdReached >= 2
        },
        {
            id: 3,
            name: 'Service Details',
            component: <ServiceDetailsForm/>,
            canJumpTo: stepIdReached >= 3
        },
        {
            id: 4,
            name: 'Summary',
            component: <ServiceSummary/>,
            nextButtonText: 'Finish',
            canJumpTo: stepIdReached >= 4
        }
    ];

    const businessInfoFormOnNextStep = (onNext) => {
        if (localStorage.getItem("tenantName") !== null && localStorage.getItem("tenantName") !== "" &&
            localStorage.getItem("orgName") !== null && localStorage.getItem("orgName") !== "" &&
            localStorage.getItem("contactName") !== null && localStorage.getItem("contactName") !== "" &&
            localStorage.getItem("orgAddress") !== null && localStorage.getItem("orgAddress") !== "" &&
            localStorage.getItem("phone") !== null && localStorage.getItem("phone") !== "") {
            setIsValidBusinessForm({
                tenantName: true,
                orgName: true,
                orgAddress: true,
                phone: true,
                contactName: true
            })
            return onNext();
        }
        var tenantNameVal = true;
        var orgNameVal = true;
        var orgAddressVal = true;
        var phoneVal = true;
        var contactNameVal = true;
        if (localStorage.getItem("tenantName") === null || localStorage.getItem("tenantName") === "") {
            tenantNameVal = false;
        }
        if (localStorage.getItem("orgName") === null || localStorage.getItem("orgName") === "") {
            orgNameVal = false;
        }
        if (localStorage.getItem("orgAddress") === null || localStorage.getItem("orgAddress") === "") {
            orgAddressVal = false;
        }
        if (localStorage.getItem("phone") === null || localStorage.getItem("phone") === "") {
            phoneVal = false;
        }
        if (localStorage.getItem("contactName") === null || localStorage.getItem("contactName") === "") {
            contactNameVal = false;
        }

        console.log("setting the object:" + JSON.stringify(isValidBusinessForm))
        setIsValidBusinessForm({
            tenantName: tenantNameVal,
            orgName: orgNameVal,
            orgAddress: orgAddressVal,
            phone: phoneVal,
            contactName: contactNameVal
        })
    }

    const CustomFooter = (
        <WizardFooter>
            <WizardContextConsumer>
                {({activeStep, goToStepByName, goToStepById, onNext, onBack, onClose}) => {
                    setStepIdReached(activeStep.id)
                    if (activeStep.id === 2) {
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
                    if (activeStep.id === 1) {
                        return (
                            <>
                                <Button variant="primary" type="submit"
                                        onClick={() => businessInfoFormOnNextStep(onNext)}>
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
                    if (activeStep.id === 3) {
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
            {isModalShowing && <ModalDialog setIsOpen={isModalShowing} data={modalData}/>}
            <Panel>
                <PanelMain>
                    <Page
                        header={Header}
                        mainContainerId={pageId}
                        style={{height: "100vh"}}
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