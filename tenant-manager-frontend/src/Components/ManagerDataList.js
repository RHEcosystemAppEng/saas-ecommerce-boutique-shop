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
            .get("/getTierCounts")
            .then((res) => {
                const receivedData = []
                res.data.forEach( arr => {
                    const tmp = []
                    tmp[0] = arr[0].charAt(0).toUpperCase() + arr[0].slice(1);
                    tmp[1] = arr[1]
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
                </Tr>
            </Thead>
            <Tbody>
                {tierData.map((arr, index) =>
                    <Tr key={index}>
                        <Td>{arr[0]}</Td>
                        <Td>{arr[1]}</Td>
                    </Tr>
                )}
            </Tbody>
        </TableComposable>
    </React.Fragment>;

};