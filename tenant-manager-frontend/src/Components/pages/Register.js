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
    const [isBtnDisabled, setIsBtnDisabled] = React.useState(true);
    const [stepIdReached, setStepIdReached] = React.useState(1);
    const [isModalShowing, setIsModalShowing] = React.useState(false);
    const [modalData, setModalData] = React.useState();
    const [isValidBusinessForm, setIsValidBusinessForm] = React.useState({
        email: true,
        password: true,
        tenantName: true,
        orgName: true,
        orgAddress: true,
        phone: true,
        contactName: true
    });
    const [isValidServiceDetailsForm, setIsValidServiceDetailsForm] = React.useState({
        hostName: true,
        avgConcurrentShoppers: true,
        peakConcurrentShoppers: true,
        fromTime: true,
        toTime: true
    });

    const activateConfirmBtn = () => {
        setIsBtnDisabled(false)
    }
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
            component: <ServiceDetailsForm isValid={isValidServiceDetailsForm}/>,
            canJumpTo: stepIdReached >= 3
        },
        {
            id: 4,
            name: 'Summary',
            component: <ServiceSummary activateConfirmBtn={activateConfirmBtn}/>,
            nextButtonText: 'Finish',
            canJumpTo: stepIdReached >= 4
        }
    ];

    const businessInfoFormOnNext = (onNext) => {
        if (localStorage.getItem("email") !== null && localStorage.getItem("email") !== "" &&
            localStorage.getItem("password") !== null && localStorage.getItem("password") !== "" &&
            localStorage.getItem("tenantName") !== null && localStorage.getItem("tenantName") !== "" &&
            localStorage.getItem("orgName") !== null && localStorage.getItem("orgName") !== "" &&
            localStorage.getItem("contactName") !== null && localStorage.getItem("contactName") !== "" &&
            localStorage.getItem("orgAddress") !== null && localStorage.getItem("orgAddress") !== "" &&
            localStorage.getItem("phone") !== null && localStorage.getItem("phone") !== "") {
            setIsValidBusinessForm({
                email: true,
                password: true,
                tenantName: true,
                orgName: true,
                orgAddress: true,
                phone: true,
                contactName: true
            })
            return onNext();
        }
        var emailVal = true;
        var passwordVal = true;
        var tenantNameVal = true;
        var orgNameVal = true;
        var orgAddressVal = true;
        var phoneVal = true;
        var contactNameVal = true;
        if (localStorage.getItem("email") === null || localStorage.getItem("email") === "") {
            emailVal = false;
        }
        if (localStorage.getItem("password") === null || localStorage.getItem("password") === "") {
            passwordVal = false;
        }
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

        setIsValidBusinessForm({
            email: emailVal,
            password: passwordVal,
            tenantName: tenantNameVal,
            orgName: orgNameVal,
            orgAddress: orgAddressVal,
            phone: phoneVal,
            contactName: contactNameVal
        })
    }

    const serviceDetailsFormOnNext = (onNext) => {
        if (localStorage.getItem("hostName") !== null && localStorage.getItem("hostName") !== "" &&
            localStorage.getItem("avgConcurrentShoppers") !== null && localStorage.getItem("avgConcurrentShoppers") !== "" &&
            localStorage.getItem("peakConcurrentShoppers") !== null && localStorage.getItem("peakConcurrentShoppers") !== "" &&
            localStorage.getItem("fromTime") !== null && localStorage.getItem("fromTime") !== "" &&
            localStorage.getItem("toTime") !== null && localStorage.getItem("toTime") !== "") {
            setIsValidServiceDetailsForm({
                hostName: true,
                avgConcurrentShoppers: true,
                peakConcurrentShoppers: true,
                fromTime: true,
                toTime: true
            })
            return onNext();
        }
        var hostNameVal = true;
        var avgConcurrentShoppersVal = true;
        var peakConcurrentShoppersVal = true;
        var fromTimeVal = true;
        var toTimeVal = true;
        if (localStorage.getItem("hostName") === null || localStorage.getItem("hostName") === "") {
            hostNameVal = false;
        }
        if (localStorage.getItem("avgConcurrentShoppers") === null || localStorage.getItem("avgConcurrentShoppers") === ""
            && Number(localStorage.getItem("avgConcurrentShoppers")) === 0) {
            avgConcurrentShoppersVal = false;
        }
        if (localStorage.getItem("peakConcurrentShoppers") === null || localStorage.getItem("peakConcurrentShoppers") === ""
            && Number(localStorage.getItem("peakConcurrentShoppers")) === 0) {
            peakConcurrentShoppersVal = false;
        }
        if (localStorage.getItem("fromTime") === null || localStorage.getItem("fromTime") === "") {
            fromTimeVal = false;
        }
        if (localStorage.getItem("toTime") === null || localStorage.getItem("toTime") === "") {
            toTimeVal = false;
        }

        setIsValidServiceDetailsForm({
            hostName: hostNameVal,
            avgConcurrentShoppers: avgConcurrentShoppersVal,
            peakConcurrentShoppers: peakConcurrentShoppersVal,
            fromTime: fromTimeVal,
            toTime: toTimeVal
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
                                        onClick={() => businessInfoFormOnNext(onNext)}>
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
                                <Button variant="primary" type="submit"
                                        onClick={() => serviceDetailsFormOnNext(onNext)}>
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