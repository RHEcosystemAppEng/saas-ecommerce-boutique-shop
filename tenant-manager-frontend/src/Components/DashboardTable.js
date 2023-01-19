import React, {useEffect} from 'react';
import {Caption, TableComposable, Tbody, Td, Tr} from '@patternfly/react-table';
import axios from "../axios-middleware";
import {Panel, PanelMain, PanelMainBody} from "@patternfly/react-core";


export const DashboardTable = (props) => {
    const [tenant, setTenant] = React.useState({});
    const [subscription, setSubscription] = React.useState({});
    const [accountBlocked, setAccountBlocked] = React.useState(false);

    useEffect(() => {
        getTenantData()
    }, [])

    const getTenantData = () => {
        axios
            .get("/tenant/" + props.tenantKey)
            .then((res) => {
                console.log(res.data)
                setTenant(res.data)
                setSubscription(res.data.subscriptions[0])
                if (res.data.status === 'Purged') {
                    setAccountBlocked(true)
                }
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }

    return <React.Fragment>
        {accountBlocked ? (
                <Panel variant="raised">
                    <PanelMain>
                        <PanelMainBody >Your subscription is no longer exists. Please contact the administrator.</PanelMainBody>
                    </PanelMain>
                </Panel>
            )
            :
            (
                <TableComposable aria-label="Simple table">
                    <Caption>Profile</Caption>
                    <Tbody>
                        <Tr key="1">
                            <Td>Email</Td>
                            <Td>{tenant.email}</Td>
                        </Tr>
                        <Tr key="2">
                            <Td>Tenant Name</Td>
                            <Td>{tenant.tenantName}</Td>
                        </Tr>
                        <Tr key="3">
                            <Td>Organization</Td>
                            <Td>{tenant.orgName}</Td>
                        </Tr>
                        <Tr key="4">
                            <Td>Tenant Status</Td>
                            <Td>{tenant.status}</Td>
                        </Tr>
                        <Tr key="5">
                            <Td>Subscription Tier</Td>
                            <Td>{subscription.tier}</Td>
                        </Tr>
                        <Tr key="6">
                            <Td>Subscription URL</Td>
                            <Td><a target="_blank" href={subscription.url}>{subscription.url}</a></Td>
                        </Tr>
                        <Tr key="7">
                            <Td>Subscription Status</Td>
                            <Td>{subscription.status}</Td>
                        </Tr>
                    </Tbody>
                </TableComposable>
            )}
    </React.Fragment>;
};