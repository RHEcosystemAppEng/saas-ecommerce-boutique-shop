import React from 'react';
import {Modal, ModalVariant, Button} from '@patternfly/react-core';
export const ModalDialog = (props) => {
    const [isModalOpen, setIsModalOpen] = React.useState(props.setIsOpen);
    const handleModalToggle = () => {
        setIsModalOpen(!isModalOpen);
    };
    return <React.Fragment>
        <Modal variant={ModalVariant.small} title={props.data.title}
               isOpen={isModalOpen} onClose={handleModalToggle}>
            {props.data.body}
        </Modal>
    </React.Fragment>;
};