import React, {useState} from 'react';
import {
    Avatar,
    Brand,
    Divider,
    Dropdown,
    DropdownGroup,
    DropdownItem,
    DropdownToggle,
    Masthead,
    MastheadBrand,
    MastheadContent,
    MastheadMain,
    Nav,
    NavItem,
    NavList,
    Page,
    PageSection,
    PageSectionVariants,
    PageSidebar,
    Text,
    TextContent,
    Toolbar,
    ToolbarContent,
    ToolbarGroup,
    ToolbarItem
} from '@patternfly/react-core';
import redHatLogo from '../../images/Logo-Red_Hat.png';
import avatar from '../../images/avatar.png';
import {useNavigate, useParams} from 'react-router-dom';
import {clearLocalStorage} from "../../Utils/Helper";
import {ManagerDataList} from "../ManagerDataList";
import {OpsPendingList} from "../OpsPendingList";

const OpsDashboard = () => {

    const navigate = useNavigate();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [activeItem, setActiveItem] = useState(0);
    const { id } = useParams();
    const onDropdownSelect = event => {
        setIsDropdownOpen(!isDropdownOpen)
    };

    const onNavSelect = result => {
        this.setState({
            activeItem: result.itemId
        });
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

    const PageNav = (
        <Nav onSelect={onNavSelect} aria-label="Nav">
            <NavList>
                <NavItem href="#" itemId={0} isActive={activeItem === 0}>
                    System panel
                </NavItem>
            </NavList>
        </Nav>
    );

    const Sidebar = <PageSidebar nav={PageNav} />;
    const pageId = 'main-content-page-layout-tertiary-nav';
    return (
        <Page
            header={Header}
            sidebar={Sidebar}
            isManagedSidebar
            isTertiaryNavWidthLimited
            isBreadcrumbWidthLimited
            mainContainerId={pageId}
            isTertiaryNavGrouped
            breadcrumbProps={{
                stickyOnBreakpoint: {
                    md: 'top'
                }
            }}
            style={{height:"100vh"}}
        >
            <PageSection variant={PageSectionVariants.light}>
                <TextContent>
                    <Text component="h1">Tenant Summary</Text>
                    <Text component="p">
                        Aggregated Silver and Gold Tier tenant data
                    </Text>
                </TextContent>
            </PageSection>
            <PageSection isWidthLimited>
                <ManagerDataList />
            </PageSection>
            <Divider/>
            <PageSection isWidthLimited>
                <OpsPendingList />
            </PageSection>
        </Page>
    );
}

export default OpsDashboard