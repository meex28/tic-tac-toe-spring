// const React = require('react');
// const ReactDOM = require('react-dom');
// const client = require('./client');

import React from 'react';
import ReactDOM from 'react-dom'

const App = () => {
    return (
        <div>
            <h1>Orzel</h1>
        </div>
    );
};

export default App;

ReactDOM.render(
    <App />,
    document.getElementById('react')
)