import React, {useEffect} from 'react';
import {TableComposable, Tbody, Td, Thead, Tr} from "@patternfly/react-table";
import axios from "../axios-middleware";

export const ManagerDataList = () => {
    const [tierData, setTierData] = React.useState([]);

    useEffect(() => {
        getTierData()
    }, [])

    const getTierData = () => {
        axios
            .get("/manager/summary")
            .then((res) => {
                const receivedData = []
                res.data.forEach( item => {
                    const tmp = []
                    tmp[0] = item.tier;
                    tmp[1] = item.totalSubscriptions;
                    tmp[2] = item.aggregatedShoppers;
                    receivedData.push(tmp)
                })
                setTierData(receivedData)
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }

    return <React.Fragment>
        <TableComposable aria-label="Simple table">
            <Thead>
                <Tr key="0">
                    <Td>Tier</Td>
                    <Td>Total Subscriptions</Td>
                    <Td>Aggregated Shopper Count</Td>
                </Tr>
            </Thead>
            <Tbody>
                {tierData.map((arr, index) =>
                    <Tr key={index}>
                        <Td>{arr[0]}</Td>
                        <Td>{arr[1]}</Td>
                        <Td>{arr[2]}</Td>
                    </Tr>
                )}
            </Tbody>
        </TableComposable>
    </React.Fragment>;

};