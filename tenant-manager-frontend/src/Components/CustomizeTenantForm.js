import React from 'react'
import {Form, FormGroup, TextInput} from '@patternfly/react-core';
import ColorPicker from "./ColorPicker";

export const CustomizeTenantForm = (props) => {
    // const [value, setValue] = React.useState(null);
    // const [filename, setFilename] = React.useState('');
    const [headingText, setHeadingText] = React.useState('');
    const [headingColor, setHeadingColor] = React.useState('');
    const [ribbonColor, setRibbonColor] = React.useState('');
    const [ribbonColorHelperText, setRibbonColorHelperText] = React.useState('');

    const handleHeadingTextChange = headingText => {
        localStorage.setItem("headingText", headingText);
        setHeadingText(headingText);
    };

    const handleHeadingColorChange = headingColor => {
        console.log("JUDE ADDED COLOR CHANGE:" + headingColor)
        localStorage.setItem("headingColor", headingColor);
        setHeadingColor(headingColor);
    };
    const handleRibbonColorChange = ribbonColor => {
        localStorage.setItem("ribbonColor", ribbonColor);
        setRibbonColor(ribbonColor);
    };

    return <React.Fragment>
        <Form isHorizontal>

            {/*<FormGroup label="Shop Logo File" fieldId="simple-form-phone-07"*/}
            {/*           helperText={headingText}>*/}
            {/*    <FileUpload id="simple-file" value={value} filename={filename}*/}
            {/*                filenamePlaceholder="Drag and drop a custom logo image file or upload one"*/}
            {/*                onFileInputChange={handleFileInputChange} onClearClick={handleClear} browseButtonText="Upload"/>*/}
            {/*</FormGroup>*/}
            <FormGroup label="Page Heading Text" fieldId="simple-form-phone-07"
                       helperText={headingText}>
                <TextInput isRequired type="text" id="simple-form-phone-07" name="simple-form-phone-07"
                           placeholder="One click away!" value={headingText} onChange={handleHeadingTextChange}/>
            </FormGroup>
            <FormGroup label="Choose ribbon color" fieldId="simple-form-email-02"
                       helperText={ribbonColorHelperText}>
                <ColorPicker colorSetter = {handleHeadingColorChange}/>
            </FormGroup>
            <FormGroup isHelperTextBeforeField={true} label="Choose heading color"
                       fieldId="simple-form-email-02"
                       helperText={ribbonColorHelperText}>
                <ColorPicker colorSetter = {handleRibbonColorChange}/>
            </FormGroup>
        </Form>
    </React.Fragment>
}
