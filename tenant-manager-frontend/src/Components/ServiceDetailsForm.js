import React from 'react';
import {Flex, FlexItem, Form, FormGroup, Popover, TextInput, TimePicker} from '@patternfly/react-core';
import HelpIcon from '@patternfly/react-icons/dist/esm/icons/help-icon';

export const ServiceDetailsForm = () => {
    const [avgConcurrentShoppers, setAvgConcurrentShoppers] = React.useState(() => {
        return localStorage.getItem("avgConcurrentShoppers") || 0;
    });
    const [peakConcurrentShoppers, setPeakConcurrentShoppers] = React.useState(() => {
        return localStorage.getItem("peakConcurrentShoppers") || 0;
    });
    const [fromTime, setFromTime] = React.useState(() => {
        return localStorage.getItem("fromTime") || "";
    });
    const [toTime, setToTime] = React.useState(() => {
        return localStorage.getItem("toTime") || "";
    });
    const handleAvgConcurrentShoppersChange = avgConcurrentShoppers => {
        localStorage.setItem("avgConcurrentShoppers", avgConcurrentShoppers);
        setAvgConcurrentShoppers(avgConcurrentShoppers);
    };
    const handlePeakConcurrentShoppersChange = peakConcurrentShoppers => {
        localStorage.setItem("peakConcurrentShoppers", peakConcurrentShoppers);
        setPeakConcurrentShoppers(peakConcurrentShoppers);
    };
    const handleFromTimeChange = fromTime => {
        localStorage.setItem("fromTime", fromTime);
        setFromTime(fromTime);
    };
    const handleToTimeChange = toTime => {
        localStorage.setItem("toTime", toTime);
        setToTime(toTime);
    };
    return <Form>

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
                        <TimePicker time="2020-10-14T18:06:02Z" id="simple-form-from-01" name="simple-form-from-01"
                                    is24Hour onChange={handleFromTimeChange} />
                    </FormGroup>
                </FlexItem>
                <FlexItem>
                    <FormGroup label="To" isRequired fieldId="simple-form-to-02">
                        <TimePicker time="2020-10-14T18:06:02Z" id="simple-form-to-02" name="simple-form-to-02"
                                    is24Hour onChange={handleToTimeChange} />
                    </FormGroup>
                </FlexItem>
            </Flex>
        </FormGroup>
    </Form>;
};