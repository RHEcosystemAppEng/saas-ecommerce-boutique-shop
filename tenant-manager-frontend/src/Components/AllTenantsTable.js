import React, {useEffect} from 'react';
import {ActionsColumn, TableComposable, Tbody, Td, Th, Thead, Tr} from '@patternfly/react-table';
import axios from "../axios-middleware";
import {Bullseye, EmptyState, EmptyStateIcon, Spinner, Title} from "@patternfly/react-core";

export const AllTenantsTable = () => {
    const [data, setData] = React.useState([]);
    const [loading, setLoading] = React.useState(true);

    const getTenantData = () => {
        axios
            .get("/tenant/")
            .then((res) => {
                setData(res.data);
                setLoading(false);
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }

    const enableTenant = (tenant) => {
        setLoading(true)
        axios
            .put("/tenant/" + tenant.tenantKey + "/enable")
            .then((res) => {
                getTenantData()
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }
    const disableTenant = (tenant) => {
        setLoading(true)
        axios
            .put("/tenant/" + tenant.tenantKey + "/disable")
            .then((res) => {
                getTenantData()
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }
    const purgeTenant = (tenant) => {
        setLoading(true)
        axios
            .put("/tenant/" + tenant.tenantKey + "/purge")
            .then((res) => {
                getTenantData()
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }

    useEffect(() => {
        getTenantData()
    }, [])

    const columnNames = {
        tenantId: 'Tenant ID',
        tenantName: 'Tenant Name',
        tier: 'Tier',
        avgConcurrentShoppers: 'Avg. Concurrent Shoppers',
        peakConcurrentShoppers: 'Peak Concurrent Shoppers',
        status: 'Status'
    };

    const getActionsBasedOnStatus = (tenant) => {
        const result = [];
        switch (tenant.status) {
            case 'Running':
                result.push({
                    title: 'Disable',
                    onClick: () => disableTenant(tenant)
                })
                result.push({
                    title: 'Purge',
                    onClick: () => purgeTenant(tenant)
                })
                break;
            case 'Requested':
                result.push({
                    title: 'Enable',
                    onClick: () => enableTenant(tenant)
                })
                break;
            case 'Stopped':
                result.push({
                    title: 'Enable',
                    onClick: () => enableTenant(tenant)
                })
                result.push({
                    title: 'Purge',
                    onClick: () => purgeTenant(tenant)
                })
                break;
        }
        return result;
    }

    return <React.Fragment>
        <TableComposable aria-label="Actions table">
            <Thead>
                <Tr>
                    <Th>{columnNames.tenantId}</Th>
                    <Th>{columnNames.tenantName}</Th>
                    <Th>{columnNames.tier}</Th>
                    <Th>{columnNames.avgConcurrentShoppers}</Th>
                    <Th>{columnNames.peakConcurrentShoppers}</Th>
                    <Th>{columnNames.status}</Th>
                    <Th>Actions</Th>
                </Tr>
            </Thead>
            <Tbody>

                {data.length === 0 || loading ?
                    (
                        <td colSpan="7">
                            <Bullseye>
                                <EmptyState>
                                    <EmptyStateIcon variant="container" component={Spinner}/>
                                    <Title size="lg" headingLevel="h2">
                                        Loading
                                    </Title>
                                </EmptyState>
                            </Bullseye>
                        </td>
                    ) : (
                        data.map(tenant => {

                            return <Tr key={tenant.name}>
                                <Td dataLabel={columnNames.tenantId}>{tenant.tenantKey}</Td>
                                <Td dataLabel={columnNames.tenantName}>{tenant.tenantName}</Td>
                                <Td dataLabel={columnNames.tier}>{tenant.tier}</Td>
                                <Td dataLabel={columnNames.avgConcurrentShoppers}>{tenant.avgConcurrentShoppers}</Td>
                                <Td dataLabel={columnNames.peakConcurrentShoppers}>{tenant.peakConcurrentShoppers}</Td>
                                <Td dataLabel={columnNames.status}>{tenant.status}</Td>
                                <Td>
                                    <ActionsColumn items={getActionsBasedOnStatus(tenant)} rowData={{}}/>
                                </Td>
                            </Tr>;
                        })
                    )
                }
            </Tbody>
        </TableComposable>
    </React.Fragment>;
};