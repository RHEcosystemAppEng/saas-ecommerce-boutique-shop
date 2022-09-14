import React, {useEffect} from 'react';
import {Caption, TableComposable, Tbody, Td, Tr} from '@patternfly/react-table';
import axios from "../axios-middleware";
import {Link} from "react-router-dom";


export const DashboardTable = (props) => {
    console.log("Tenant ID=" + props.tenantId)
    const [tenant, setTenant] = React.useState({});
    const [subscription, setSubscription] = React.useState({});

    useEffect(() => {
        getTenantData()
    }, [])

    const getTenantData = () => {
        axios
            .get("/tenant/"+ props.tenantId)
            .then((res) => {
                console.log(res.data)
                setTenant(res.data)
                setSubscription(res.data.subscriptionSet[0])
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }

    return <React.Fragment>
        <TableComposable aria-label="Simple table">
            <Caption>Profile</Caption>
            <Tbody>
                <Tr key="1">
                    <Td>Email</Td>
                    <Td>{tenant.email}</Td>
                </Tr>
                <Tr key="2">
                    <Td>Tenant Name</Td>
                    <Td>{tenant.tenantUserName}</Td>
                </Tr>
                <Tr key="3">
                    <Td>Organization</Td>
                    <Td>{tenant.orgName}</Td>
                </Tr>
                <Tr key="4">
                    <Td>Subscription Tier</Td>
                    <Td>{subscription.serviceLevel}</Td>
                </Tr>
                <Tr key="5">
                    <Td>Subscription URL</Td>
                    <Td><a target="_blank" href={subscription.url}>{subscription.url}</a></Td>
                </Tr>
                <Tr key="6">
                    <Td>Subscription Status</Td>
                    <Td>{subscription.status}</Td>
                </Tr>
            </Tbody>
        </TableComposable>
    </React.Fragment>;
};