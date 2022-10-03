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
    const [hostName, setHostName] = React.useState(() => {
        return localStorage.getItem("hostName") || "";
    });
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
            console.log('Sadu added'+localStorage.getItem("tenantKey"))
            getResourceData()
        }
    }, [])

    const getResourceData = () => {
        axios
            .get("/get-current-request/"+localStorage.getItem("tenantKey"))
            .then((res) => {
                setHostName(res.data.hostName)
                setAvgConcurrentShoppers(res.data.avgConcurrentShoppers)
                setPeakConcurrentShoppers(res.data.peakConcurrentShoppers)
                localStorage.setItem("hostName", res.data.hostName);
                localStorage.setItem("avgConcurrentShoppers", res.data.avgConcurrentShoppers);
                localStorage.setItem("peakConcurrentShoppers", res.data.peakConcurrentShoppers);
                localStorage.setItem("tier", res.data.newTier);
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
        onlyForUpdateWorkflow();
    };

    const handleAvgConcurrentShoppersChange = avgConcurrentShoppers => {
        localStorage.setItem("avgConcurrentShoppers", avgConcurrentShoppers);
        setAvgConcurrentShoppers(avgConcurrentShoppers);
        onlyForUpdateWorkflow();
    };
    const handlePeakConcurrentShoppersChange = peakConcurrentShoppers => {
        localStorage.setItem("peakConcurrentShoppers", peakConcurrentShoppers);
        setPeakConcurrentShoppers(peakConcurrentShoppers);
        onlyForUpdateWorkflow();
    };
    const handleFromTimeChange = fromTime => {
        localStorage.setItem("fromTime", fromTime);
        setFromTime(fromTime);
        onlyForUpdateWorkflow();
    };
    const handleToTimeChange = toTime => {
        localStorage.setItem("toTime", toTime);
        setToTime(toTime);
        onlyForUpdateWorkflow();
    };

    const onlyForUpdateWorkflow = () => {
        if (localStorage.getItem("tenantKey") && props && props.markDataChanged) {
            props.markDataChanged(true);
        }
    }
    return <Form>

        <FormGroup label="Preferred Domain URL" isRequired fieldId="simple-form-email-00">
            <TextInput isRequired type="text" id="simple-form-email-00" name="simple-form-email-00"
                       value={hostName}
                       onChange={handleHostNameChange} validated={isHostNameValid}
            placeholder={"my-online-shop"}/>
        </FormGroup>
        <FormGroup label="Average Concurrent Shoppers" isRequired fieldId="simple-form-email-01"
                   helperText="Expected average number of parallel users in the system.">
            <TextInput isRequired type="number" id="simple-form-email-01" name="simple-form-email-01"
                       value={avgConcurrentShoppers}
                       onChange={handleAvgConcurrentShoppersChange}/>
        </FormGroup>
        <FormGroup label="Peak Concurrent Shoppers" isRequired fieldId="simple-form-email-02">
            <TextInput isRequired type="number" id="simple-form-email-02" name="simple-form-email-02"
                       value={peakConcurrentShoppers}
                       onChange={handlePeakConcurrentShoppersChange}/>
        </FormGroup>
        <FormGroup label="Peak Shopping Window" fieldId="simple-form-email-03" labelIcon={<Popover headerContent={
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