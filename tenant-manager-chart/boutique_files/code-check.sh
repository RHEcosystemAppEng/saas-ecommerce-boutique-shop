function deleteNamespace() {
    ns=$1
    oc delete namespace ${ns}
}

## Returns 0 if the NS was missing, 1 otherwise
echo "Removing namespace ${ns}"
ns=$1
returncode=1
if [ $? -eq 0 ]; then
    log "The tenant namespace ${ns},exist. We are deleting it."
    oc delete namespace ${ns}
fi

