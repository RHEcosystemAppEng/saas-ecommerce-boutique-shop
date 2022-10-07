import {BrowserRouter, Route, Routes} from "react-router-dom"
import PrivateRoutes from "./Components/util/PrivateRoutes";
import {Login} from "./Components/pages/Login"
import {Register} from "./Components/pages/Register"
import {UpdateResources} from "./Components/pages/UpdateResources"
import Dashboard from "./Components/pages/Dashboard"
import ManagerDashboard from "./Components/pages/ManagerDashboard"
import OpsDashboard from "./Components/pages/OpsDashboard"

function App() {
    return (
        <div className="App">
                <BrowserRouter>
                    <Routes>
                        <Route path="/*" element={<Login/>}/>
                        <Route path="/register" element={<Register/>}/>
                        <Route element={<PrivateRoutes/>}>
                            <Route path="/update-limits/:id" element={<UpdateResources/>}/>
                            <Route path="/dashboard/:id" element={<Dashboard/>}/>
                            <Route path="/mgr-dashboard" element={<ManagerDashboard/>}/>
                            <Route path="/ops-dashboard" element={<OpsDashboard/>}/>
                        </Route>
                    </Routes>
                </BrowserRouter>
        </div>
    );
}

export default App;
