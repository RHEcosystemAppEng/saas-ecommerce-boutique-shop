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
import {useNavigate, useParams} from "react-router-dom";
import {clearLocalStorage} from "../../Utils/Helper";

export const UpdateResources = (props) => {
    const navigate = useNavigate();
    const [isPrimaryLoading, setIsPrimaryLoading] = React.useState(false);
    const [isBtnDisabled, setIsBtnDisabled] = React.useState(false);
    const [resourceData, setResourceData] = React.useState([]);
    const [tier, setTier] = React.useState("");
    const [peakConcurrentShoppers, setPeakConcurrentShoppers] = React.useState(0);
    const [avgConcurrentShoppers, setAvgConcurrentShoppers] = React.useState(0);
    const { id } = useParams();

    useEffect(() => {
        getResourceData()
    }, [])

    const getResourceData = () => {
        axios
            .get("/get-current-request/"+localStorage.getItem("tenantKey"))
            .then((res) => {
                setResourceData(res.data)
                setTier(res.data.newTier)
                setAvgConcurrentShoppers(res.data.avgConcurrentShoppers)
                setPeakConcurrentShoppers(res.data.peakConcurrentShoppers)
                localStorage.setItem("peakConcurrentShoppers", res.data.peakConcurrentShoppers);
                localStorage.setItem("avgConcurrentShoppers", res.data.avgConcurrentShoppers);
                localStorage.setItem("tier", res.data.newTier);
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
            "tier": localStorage.getItem("tier") || "",
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
            component: <ServiceDetailsForm peakConcurrentShoppers={peakConcurrentShoppers} avgConcurrentShoppers={avgConcurrentShoppers}/>
        },
        {
            id: 2,
            name: 'Summary',
            component: <ServiceSummary tier={tier}/>,
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
                                <a href={"/dashboard/"+id}>
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