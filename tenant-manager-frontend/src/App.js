import {BrowserRouter, Route, Routes} from "react-router-dom"
import {Login} from "./Components/pages/Login"
import {Register} from "./Components/pages/Register"
import {UpdateResources} from "./Components/pages/UpdateResources"
import Dashboard from "./Components/pages/Dashboard"
import ManagerDashboard from "./Components/pages/ManagerDashboard"

function App() {
    return (
        <div className="App">
                <BrowserRouter>
                    <Routes>
                        <Route path="/*" element={<Login/>}/>
                        <Route path="/register" element={<Register/>}/>
                        <Route path="/update-limits" element={<UpdateResources/>}/>
                        <Route path="/dashboard/:id" element={<Dashboard/>}/>
                        <Route path="/mgr-dashboard" element={<ManagerDashboard/>}/>
                    </Routes>
                </BrowserRouter>
        </div>
    );
}

export default App;
