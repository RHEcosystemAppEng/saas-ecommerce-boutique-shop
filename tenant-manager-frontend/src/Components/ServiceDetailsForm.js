import React,{useEffect} from 'react';
import {
    Flex,
    FlexItem,
    Form,
    FormGroup,
    Popover,
    TextInput,
    TimePicker,
    ValidatedOptions
} from '@patternfly/react-core';
import HelpIcon from '@patternfly/react-icons/dist/esm/icons/help-icon';
import axios from "../axios-middleware";

export const ServiceDetailsForm = (props) => {
    const [disableHostName, setDisableHostName] = React.useState(!!props.disableHostName);
    const [hostName, setHostName] = React.useState(() => {
        return localStorage.getItem("hostName") || "";
    });
    const [hostNameHelperText, setHostNameHelperText] = React.useState("");
    const [avgConcurrentShoppersHelperText, setAvgConcurrentShoppersHelperText] =
        React.useState("Expected average number of parallel users in the system.");
    const [peakConcurrentShoppersHelperText, setPeakConcurrentShoppersHelperText] = React.useState("");
    const [fromTimeHelperText, setFromTimeHelperText] = React.useState("");
    const [toTimeHelperText, setToTimeHelperText] = React.useState("");
    const [avgConcurrentShoppers, setAvgConcurrentShoppers] = React.useState(() => {
        return localStorage.getItem("avgConcurrentShoppers") || 0;
    });
    const [peakConcurrentShoppers, setPeakConcurrentShoppers] = React.useState(() => {
        return localStorage.getItem("peakConcurrentShoppers") || 0;
    });
    const [fromTime, setFromTime] = React.useState(() => {
        return localStorage.getItem("fromTime") || new Date();
    });
    const [toTime, setToTime] = React.useState(() => {
        return localStorage.getItem("toTime") || new Date();
    });
    // validation attrs
    const [isHostNameValid, setIsHostNameValid] = React.useState(ValidatedOptions.default);

    useEffect(() => {
        if (localStorage.getItem("tenantKey") && props && !props.isEdited) {
            getResourceData()
        }
    }, [])

    useEffect(() => {
        if (props.isValid && !props.isValid.hostName) {
            setHostNameHelperText("Invalid host name")
        }
        if (props.isValid && !props.isValid.avgConcurrentShoppers) {
            setAvgConcurrentShoppersHelperText("Invalid average concurrent shoppers count")
        }
        if (props.isValid && !props.isValid.peakConcurrentShoppers) {
            setPeakConcurrentShoppersHelperText("Invalid peak concurrent shoppers count")
        }
        if (props.isValid && !props.isValid.fromTime) {
            setFromTimeHelperText("Invalid from time value")
        }
        if (props.isValid && !props.isValid.toTime) {
            setToTimeHelperText("Invalid to time value")
        }

    }, [props.isValid])

    const getResourceData = () => {
        axios
            .get("/subscribe/"+localStorage.getItem("tenantKey"))
            .then((res) => {
                setHostName(res.data.request.hostName)
                setAvgConcurrentShoppers(res.data.request.avgConcurrentShoppers)
                setPeakConcurrentShoppers(res.data.request.peakConcurrentShoppers)
                localStorage.setItem("hostName", res.data.request.hostName);
                localStorage.setItem("avgConcurrentShoppers", res.data.request.avgConcurrentShoppers);
                localStorage.setItem("peakConcurrentShoppers", res.data.request.peakConcurrentShoppers);
                localStorage.setItem("tier", res.data.request.tier);
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }

    const handleHostNameChange = hostName => {
        if (hostName.length === 0) {
            setIsHostNameValid(ValidatedOptions.warning)
        } else if(hostName.match("\\s"))
            setIsHostNameValid(ValidatedOptions.error)
        else
            setIsHostNameValid(ValidatedOptions.success)
        localStorage.setItem("hostName", hostName);
        setHostName(hostName);
        setHostNameHelperText("");
        onlyForUpdateWorkflow();
    };

    const handleAvgConcurrentShoppersChange = avgConcurrentShoppers => {
        localStorage.setItem("avgConcurrentShoppers", avgConcurrentShoppers);
        setAvgConcurrentShoppers(avgConcurrentShoppers);
        setAvgConcurrentShoppersHelperText("");
        onlyForUpdateWorkflow();
    };
    const handlePeakConcurrentShoppersChange = peakConcurrentShoppers => {
        localStorage.setItem("peakConcurrentShoppers", peakConcurrentShoppers);
        setPeakConcurrentShoppers(peakConcurrentShoppers);
        setPeakConcurrentShoppersHelperText("");
        onlyForUpdateWorkflow();
    };
    const handleFromTimeChange = fromTime => {
        localStorage.setItem("fromTime", fromTime);
        setFromTime(fromTime);
        setFromTimeHelperText("");
        onlyForUpdateWorkflow();
    };
    const handleToTimeChange = toTime => {
        localStorage.setItem("toTime", toTime);
        setToTime(toTime);
        setToTimeHelperText("");
        onlyForUpdateWorkflow();
    };

    const onlyForUpdateWorkflow = () => {
        if (localStorage.getItem("tenantKey") && props && props.markDataChanged) {
            props.markDataChanged(true);
        }
    }
    return <Form>

        <FormGroup label="Preferred Domain URL" isRequired fieldId="simple-form-email-00"
        helperText={hostNameHelperText}>
            <TextInput isRequired type="text" id="simple-form-email-00" name="simple-form-email-00"
                       value={hostName}
                       isDisabled={disableHostName}
                       onChange={handleHostNameChange} validated={isHostNameValid}
            placeholder={"my-online-shop"}/>
        </FormGroup>
        <FormGroup label="Average Concurrent Shoppers" isRequired fieldId="simple-form-email-01"
                   helperText={avgConcurrentShoppersHelperText}>
            <TextInput isRequired type="number" id="simple-form-email-01" name="simple-form-email-01"
                       value={avgConcurrentShoppers}
                       onChange={handleAvgConcurrentShoppersChange}/>
        </FormGroup>
        <FormGroup label="Peak Concurrent Shoppers" isRequired fieldId="simple-form-email-02"
        helperText={peakConcurrentShoppersHelperText}>
            <TextInput isRequired type="number" id="simple-form-email-02" name="simple-form-email-02"
                       value={peakConcurrentShoppers}
                       onChange={handlePeakConcurrentShoppersChange}/>
        </FormGroup>
        <FormGroup label="Peak Shopping Window" fieldId="simple-form-email-03"
                   helperText={fromTimeHelperText + "   " +toTimeHelperText}
                   labelIcon={<Popover headerContent={
            <div>
                Peak Time Frame
            </div>} bodyContent={<div>
            Anticipated time window for the most number of shoppers in the online shop.
        </div>}>
            <button type="button" aria-label="More info for name field" onClick={e => e.preventDefault()}
                    aria-describedby="simple-form-name-04" className="pf-c-form__group-label-help">
                <HelpIcon noVerticalAlign/>
            </button>
        </Popover>}>
            <Flex>
                <FlexItem>
                    <FormGroup label="From" isRequired fieldId="simple-form-from-01">
                        <TimePicker time={fromTime} id="simple-form-from-01" name="simple-form-from-01"
                                    is24Hour onChange={handleFromTimeChange} />
                    </FormGroup>
                </FlexItem>
                <FlexItem>
                    <FormGroup label="To" isRequired fieldId="simple-form-to-02">
                        <TimePicker time={toTime} id="simple-form-to-02" name="simple-form-to-02"
                                    is24Hour onChange={handleToTimeChange} />
                    </FormGroup>
                </FlexItem>
            </Flex>
        </FormGroup>
    </Form>;
};