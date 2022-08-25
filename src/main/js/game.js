import React, {useEffect, useState} from 'react';
import GameBoard from "./game-board";
import Scoreboard from "./scoreboard";
import SockJsClient from 'react-stomp'
import {useLocation} from "react-router";

const Game = () => {
    const location = useLocation();

    const [board, setBoard] = useState(location.state.game.board);
    const [opponent, setOpponent] = useState(location.state.game.opponent)
    const [result, setResult] = useState({"host": location.state.game.ownResult, "opponent": location.state.game.opponentResult})
    const [status, setStatus] = useState(location.state.game.status)

    useEffect(() =>{
        console.log("GAME")
        console.log(location.state.game)
    }, [])

    // Send request with clicking
    const clickField = (field) =>{
        console.log(field)
    }

    const handleMessage = (msg, topic) =>{
        setStatus(msg.status)
        setResult({
            "host": msg.ownResult,
            "opponent": msg.opponentResult
        })
        setBoard(msg.board)
        setOpponent(msg.opponent)

        if(status === "OPPONENT_LEFT"){
            alert("Opponent left game!")
            setStatus("WAITING_FOR_OPPONENT")
            //TODO: clear board (?)
        }
    }

    // Send request to leave and navigate to menu
    const leaveGame = () =>{
        console.log("LEAVE")
    }

    const setReady = async () =>{
        let response = await fetch("/api/game/ready",{
            method: 'POST',
            body: location.state.token
        });

        if(response.status !== 200){
            alert(response.json().message)
        }
    }

    const readyButton = () =>{
        if(status === 'NOT_READY'){
            return <button onClick={setReady}>Ready</button>
        }
    }

    return (
        <div>
            {/*TODO: change url to window.location*/}
            <SockJsClient url={"/ws-register"}
                          topics={["/topic/"+location.state.token]}
                          onMessage={handleMessage}
                        debug={true}/>
            <Scoreboard host={location.state.host} opponent={opponent} result={result} status={status}/>
            <GameBoard board={board} handleClick={clickField}/>
            <button onClick={leaveGame}>Leave</button>
            {readyButton()}
        </div>
    );

};

export default Game;