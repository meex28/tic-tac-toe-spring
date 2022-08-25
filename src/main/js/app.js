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
import GameBoard from "./game-board";
import Game from "./game";

import './main.css'

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
        </div>
    );
};

export default App;

ReactDOM.render(
    <App />,
    document.getElementById('react')
)