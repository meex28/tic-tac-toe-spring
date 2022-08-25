import React from 'react';

const Scoreboard = (props) => {
    return (
        <div>
            <div className="scoreboard">
                <div>
                    <h2>{props.host}</h2>
                </div>
                <div>
                    <h2>{props.result.host} : {props.result.opponent}</h2>
                </div>
                <div>
                    <h2>{props.opponent}</h2>
                </div>
            </div>
            <div className="status">
                <h3>{props.status}</h3>
            </div>
        </div>
    );
};

export default Scoreboard;