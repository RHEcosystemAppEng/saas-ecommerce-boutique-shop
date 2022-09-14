function clearLocalStorage() {
    localStorage.removeItem("email")
    localStorage.removeItem("password")
    localStorage.removeItem("confPassword")
    localStorage.removeItem("tenantName")
    localStorage.removeItem("orgName")
    localStorage.removeItem("orgAddress")
    localStorage.removeItem("phone")
    localStorage.removeItem("contactName")
    localStorage.removeItem("tier")
    localStorage.removeItem("avgConcurrentShoppers")
    localStorage.removeItem("peakConcurrentShoppers")
    localStorage.removeItem("fromTime")
    localStorage.removeItem("toTime")
    localStorage.removeItem("loggedInUserName")
    localStorage.removeItem("freeTier")
    localStorage.removeItem("silverTier")
    localStorage.removeItem("goldTier")
}

module.exports = {
    clearLocalStorage
}