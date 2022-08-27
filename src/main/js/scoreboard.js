import React from 'react';
import Score from "./score";

const Scoreboard = (props) => {
    const parseStatus = (s) =>{
        s = s.replaceAll('_', ' ').toLowerCase();
        return s.charAt(0).toUpperCase() + s.slice(1);
    }

    return (
        <div>
            <div className="scoreboard">
                <Score player={props.host} score={props.result.host}/>
                <Score player={props.opponent} score={props.result.opponent}/>
            </div>
            <div className="status">
                <h3>{parseStatus(props.status)}</h3>
            </div>
        </div>
    );
};

export default Scoreboard;