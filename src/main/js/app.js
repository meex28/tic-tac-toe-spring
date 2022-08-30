import React, {useState} from 'react';
import ReactDOM from 'react-dom'
import {
    BrowserRouter as Router,
    Routes,
    Route,
    Link
} from "react-router-dom";

import StartComponent from "./startComponent";
import Menu from "./menu";
import Game from "./game";

import './main.css'
import toast, {Toaster} from "react-hot-toast";

const App = () => {
    return (
        <div className="App">
            <Router>
                <div>
                    <Routes>
                        <Route exact path="/"
                               element={<StartComponent/>}/>
                        <Route exact path="/menu"
                               element={<Menu/>}/>
                        <Route exact path="/game"
                               element={<Game/>}/>
                    </Routes>
                </div>
            </Router>
            <Toaster position={'top-center'} toastOptions={{
                duration: 1000
            }}/>
            {/*<div>*/}
            {/*    <button onClick={notify}>Make me a toast</button>*/}
            {/*    <Toaster />*/}
            {/*</div>*/}
        </div>
    );
};

export default App;

ReactDOM.render(
    <App />,
    document.getElementById('react')
)