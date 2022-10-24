import React from "react";
import {Td, Tr} from "@patternfly/react-table";
import {Button, OverflowMenu, OverflowMenuContent, OverflowMenuGroup, OverflowMenuItem} from "@patternfly/react-core";
import axios from "../axios-middleware";

export const PendingListRow = (props) => {
    const [visibleActions, setVisibleActions] = React.useState(props.visibleActions);
    const [requestId, setRequestId] = React.useState(props.rowData.requestId);
    const [isVisible, setIsVisible] = React.useState(true);
    const columnNames = {
        tenantKey: "Tenant ID",
        tenantName: "Tenant Name",
        currentTier: "Current Tier",
        newTier: "Requesting Tier",
        serviceName: "Service Name",
        oldMinInstances: "Prev. Min Instances",
        newMinInstances: "New Min Instances",
        oldMaxInstances: "Prev. Max Instances",
        newMaxInstances: "New Max Instances"
    };

    const onApprove = () => {
        axios
            .put("/request/" + requestId + "/approve")
            .then((res) => {
                console.log('Approved response:' + JSON.stringify(res.data))
                if (res.data.status === 'APPROVED') {
                    setIsVisible(false);
                }else {
                    console.log("DIDN't APPROVED")
                }
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }

    const onReject = () => {
        axios
            .put("/request/" + requestId + "/reject")
            .then((res) => {
                if (res.data.status === 'REJECTED') {
                    setIsVisible(false);
                }else {
                    console.log("DIDN't REJECTED")
                }
            })
            .catch((err) => {
                console.error(JSON.stringify(err))
            })
    }

    return (isVisible &&
        <Tr key={props.rowData.requestId}>
            <Td dataLabel={columnNames.tenantKey}>{props.rowData.tenantKey}</Td>
            <Td dataLabel={columnNames.tenantName}>{props.rowData.tenantName}</Td>
            <Td dataLabel={columnNames.currentTier}>{props.rowData.currentTier}</Td>
            <Td dataLabel={columnNames.newTier}>{props.rowData.newTier}</Td>
            <Td dataLabel={columnNames.serviceName}>{props.rowData.serviceName}</Td>
            <Td dataLabel={columnNames.oldMinInstances}>{props.rowData.oldMinInstances}</Td>
            <Td dataLabel={columnNames.newMinInstances}>{props.rowData.newMinInstances}</Td>
            <Td dataLabel={columnNames.oldMaxInstances}>{props.rowData.oldMaxInstances}</Td>
            <Td dataLabel={columnNames.newMaxInstances}>{props.rowData.newMaxInstances}</Td>
            {visibleActions &&
                <Td isActionCell>
                    <OverflowMenu breakpoint="lg">
                        <OverflowMenuContent>
                            <OverflowMenuGroup groupType="button">
                                <OverflowMenuItem>
                                    <Button variant="primary" onClick={onApprove}>Approve</Button>
                                </OverflowMenuItem>
                                <OverflowMenuItem>
                                    <Button variant="tertiary" onClick={onReject}>Reject</Button>
                                </OverflowMenuItem>
                            </OverflowMenuGroup>
                        </OverflowMenuContent>
                    </OverflowMenu>
                </Td>
            }

        </Tr>);
}