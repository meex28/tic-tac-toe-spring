import React from 'react';

const Score = ({player, score}) => {
    return (
        <div className={"score"}>
            <div className={"scoreboard-name"}>{player}</div>
            <div className={"scoreboard-score"}>{score}</div>
        </div>
    );
};

export default Score;