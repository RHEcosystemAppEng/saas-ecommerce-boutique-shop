import React from 'react'
import {FileUpload, Form, FormGroup, TextInput} from '@patternfly/react-core';
import ColorPicker from "./ColorPicker";

export const CustomizeTenantForm = (props) => {
    const [value, setValue] = React.useState(null);
    const [filename, setFilename] = React.useState('');
    const [headingText, setHeadingText] = React.useState('');
    const [ribbonColorHelperText, setRibbonColorHelperText] = React.useState('');
    const handleFileInputChange = (_event, file) => {
        setFilename(file.name);
    };
    const handleClear = _event => {
        setFilename('');
        setValue('');
    };
    return <React.Fragment>
        <Form isHorizontal>

            <FormGroup label="Shop Logo File" fieldId="simple-form-phone-07"
                       helperText={headingText}>
                <FileUpload id="simple-file" value={value} filename={filename}
                            filenamePlaceholder="Drag and drop a custom logo image file or upload one"
                            onFileInputChange={handleFileInputChange} onClearClick={handleClear} browseButtonText="Upload"/>
            </FormGroup>
            <FormGroup label="Page Heading Text" fieldId="simple-form-phone-07"
                       helperText={headingText}>
                <TextInput isRequired type="text" id="simple-form-phone-07" name="simple-form-phone-07"
                           placeholder="One click away!" value={headingText} onChange={setHeadingText}/>
            </FormGroup>
            <FormGroup label="Choose ribbon color" fieldId="simple-form-email-02"
                       helperText={ribbonColorHelperText}>
                <ColorPicker/>
            </FormGroup>
            <FormGroup isHelperTextBeforeField={true} label="Choose heading color"
                       fieldId="simple-form-email-02"
                       helperText={ribbonColorHelperText}>
                <ColorPicker/>
            </FormGroup>
        </Form>
    </React.Fragment>
}