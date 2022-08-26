import React, {useEffect, useState} from 'react';
import GameBoard from "./game-board";
import Scoreboard from "./scoreboard";
import SockJsClient from 'react-stomp'
import {useLocation} from "react-router";
import {useNavigate} from "react-router-dom";

const Game = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const [board, setBoard] = useState(location.state.game.board);
    const [opponent, setOpponent] = useState(location.state.game.opponent)
    const [result, setResult] = useState({"host": location.state.game.ownResult, "opponent": location.state.game.opponentResult})
    const [status, setStatus] = useState(location.state.game.status)

    const endedGameStatuses = ['DRAW', 'OPPONENT_WON', 'YOU_WON']

    useEffect(() =>{
        console.log("GAME")
        console.log(location.state.game)
    }, [])

    // Send request with clicking
    const clickField = async (field) =>{
        //TODO: check if field is empty

        if(status !== 'YOUR_TURN')
            return;

        const params = new URLSearchParams();
        params.append("field", field)

        let response = await fetch("/api/game/move?"+params.toString(),{
            method: 'POST',
            body: location.state.token
        });

        if(response.status !== 200){
            alert(response.json().message)
        }
    }

    const handleMessage = (msg, topic) =>{
        if(endedGameStatuses.includes(status)){
            if(["OPPONENT_LEFT", "GAME_ENDED", "OPPONENT_NOT_READY",
                "YOUR_TURN", "OPPONENT_TURN"].includes(msg.status))
                setStatus(msg.status)
        }else
            setStatus(msg.status)


        setResult({
            "host": msg.ownResult,
            "opponent": msg.opponentResult
        })
        setBoard(msg.board)
        setOpponent(msg.opponent)

        if(msg.status === "OPPONENT_LEFT"){
            alert("Opponent left game!")
            setStatus("WAITING_FOR_OPPONENT")
        }else if(msg.status === "GAME_ENDED"){
            alert("Host left session. Session is deleted.")
            navigate(-1)
        }
    }

    // Send request to leave and navigate to menu
    const leaveGame = async () =>{
        let response = await fetch("/api/game/leave",{
            method: 'POST',
            body: location.state.token
        });

        if(response.status !== 200){
            alert(response.json().message)
        }else{
            navigate(-1)
        }
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
        if(endedGameStatuses.includes(status) || status === 'NOT_READY'){
            return <button onClick={setReady}>Ready</button>
        }
    }

    return (
        <div>
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