import React, {useEffect} from 'react';
import {TableComposable, Tbody, Td, Th, Thead, Tr} from "@patternfly/react-table";
import axios from "../axios-middleware";
import {PendingListRow} from "./PendingListRow";

export const ManagerPendingList = () => {
    useEffect(() => {
        getTierData()
    }, [])

    const getTierData = () => {
        axios
            .get("/request/pending")
            .then((res) => {
                setDataList(res.data);
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }

    const [dataList, setDataList] = React.useState([]);

    return <React.Fragment>
        <TableComposable aria-label="Actions table">
            <Thead>
                <Tr>
                    <Th modifier="wrap">Tenant ID</Th>
                    <Th modifier="wrap">Tenant Name</Th>
                    <Th modifier="wrap">Current Tier</Th>
                    <Th modifier="wrap">Requesting Tier</Th>
                    <Th modifier="wrap">Service Name</Th>
                    <Th modifier="wrap">Prev. Min Instances</Th>
                    <Th modifier="wrap">Prev. Max Instances</Th>
                    <Th modifier="wrap">New Min Instances</Th>
                    <Th modifier="wrap">New Max Instances</Th>
                </Tr>
            </Thead>
            <Tbody>
                {dataList.map((rowData, index) => (
                    <PendingListRow visibleActions={false} rowData={rowData}/>
                ))}
            </Tbody>
        </TableComposable>
    </React.Fragment>;

};