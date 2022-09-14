import {BrowserRouter, Route, Routes} from "react-router-dom"
import {Login} from "./Components/pages/Login"
import {Register} from "./Components/pages/Register"
import Dashboard from "./Components/pages/Dashboard"

function App() {
    return (
        <div className="App">
                <BrowserRouter>
                    <Routes>
                        <Route path="/*" element={<Login/>}/>
                        <Route path="/register" element={<Register/>}/>
                        <Route path="/dashboard/:id" element={<Dashboard/>}/>
                    </Routes>
                </BrowserRouter>
        </div>
    );
}

export default App;
