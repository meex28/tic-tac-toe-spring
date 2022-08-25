import React, {useEffect, useState} from 'react';
import {useLocation} from "react-router";
import {useNavigate} from "react-router-dom";

const Menu = () => {
    const location = useLocation();
    const [nickname, setNickname] = useState(location.state.nickname);
    const [token, setToken] = useState(location.state.token);
    const navigate = useNavigate();

    // send request to start new game and navigate to new page
    const startNewGame = async () =>{
        let game = await fetch("/api/game/join",{
            method: 'POST',
            body: token
        }).then((response) => response.json());

        navigate('/game', {
            state: {
                game: game,
                token: token,
                host: nickname
            }
        })
    }

    // navigate to page with input for session ID
    const joinGame = async () =>{
        let opponentToken = prompt("Give opponent token: ");
        if (opponentToken == null){
            alert("Token cannot be empty!");
            return;
        }

        let game = await fetch("/api/game/join/"+opponentToken,{
            method: 'POST',
            body: token
        }).then((response) => response.json());

        console.log(game);

        navigate('/game', {
            state: {
                game: game,
                token: token,
                host: nickname
            }
        });
    }

    // send request to start new AI game and navigate to new page
    const startNewAiGame = () =>{

    }

    return (
        <div>
            <h1>{nickname}</h1>
            <p>Your token: {token}</p>
            <button onClick={startNewGame}>Start new game</button>
            <button onClick={joinGame}>Join game</button>
            <button onClick={startNewAiGame}>Start game with AI</button>
        </div>
    );
};

export default Menu;