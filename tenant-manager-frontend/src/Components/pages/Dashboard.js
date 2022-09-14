import React, {useState} from 'react';
import {
    Avatar,
    Brand,
    Dropdown,
    DropdownGroup,
    DropdownItem,
    DropdownToggle,
    Masthead,
    MastheadBrand,
    MastheadContent,
    MastheadMain,
    Page,
    PageSection,
    PageSectionVariants,
    Panel,
    PanelMain,
    PanelMainBody,
    Text,
    TextContent,
    Toolbar,
    ToolbarContent,
    ToolbarGroup,
    ToolbarItem
} from '@patternfly/react-core';
import redHatLogo from '../../images/Logo-Red_Hat.png';
import avatar from '../../images/avatar.png';
import {DashboardTable} from '../DashboardTable'
import { useParams } from 'react-router-dom';
import {useNavigate} from "react-router-dom"
import {clearLocalStorage} from "../../Utils/Helper";
const Dashboard = () => {

    const navigate = useNavigate();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const { id } = useParams();
    const onDropdownSelect = event => {
        setIsDropdownOpen(!isDropdownOpen)
    };

    const clearLocalStorageData = () => {

        clearLocalStorage();
        navigate("/login")
    }

    const userDropdownItems = [
        <DropdownGroup key="group 2">
            <DropdownItem key="group 2 logout" onClick={clearLocalStorageData}>Logout</DropdownItem>
        </DropdownGroup>
    ];

    const headerToolbar = (
        <Toolbar id="toolbar" isFullHeight isStatic>
            <ToolbarContent>
                <ToolbarGroup
                    variant="icon-button-group"
                    alignment={{default: 'alignRight'}}
                    spacer={{default: 'spacerNone', md: 'spacerMd'}}
                >
                    <ToolbarItem visibility={{default: 'hidden', md: 'visible'}}>
                        <Dropdown
                            position="right"
                            onSelect={onDropdownSelect}
                            isOpen={isDropdownOpen}
                            toggle={
                                <DropdownToggle icon={<Avatar src={avatar} alt="Avatar"/>} onToggle={setIsDropdownOpen}>
                                    {localStorage.getItem("loggedInUserName")}
                                </DropdownToggle>
                            }
                            dropdownItems={userDropdownItems}
                        />
                    </ToolbarItem>
                </ToolbarGroup>

            </ToolbarContent>
        </Toolbar>
    );

    const Header = (
        <Masthead>
            <MastheadMain>
                <MastheadBrand>
                    <Brand src={redHatLogo} alt="Red Hat logo">
                        <source srcSet={redHatLogo} width={"150px"}/>
                    </Brand>
                </MastheadBrand>
            </MastheadMain>
            <MastheadContent>{headerToolbar}</MastheadContent>
        </Masthead>
    );

    return (
        <Page
            header={Header}
            additionalGroupedContent={
                <PageSection variant={PageSectionVariants.light}>
                    <TextContent>
                        <Text component="h1">Subscription Information</Text>
                        <Text component="p">
                            You have the following subscription registered to your account.
                        </Text>
                    </TextContent>
                </PageSection>
            }
        >
            <PageSection>
                <Panel>
                    <PanelMain>
                        <PanelMainBody>
                            <DashboardTable tenantId={id}/>
                        </PanelMainBody>
                    </PanelMain>
                </Panel>
            </PageSection>
        </Page>
    );
}

export default Dashboard