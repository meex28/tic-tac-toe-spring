import React, {useEffect, useState} from 'react';
import {useLocation} from "react-router";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";

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

        let isReponseOK = true;

        let game = await fetch("/api/game/join/"+opponentToken,{
            method: 'POST',
            body: token
        }).then((response) => {
            if(!response.ok){
                throw Error("Invalid token!");
            }else{
                return response.json();
            }
        }).catch((err) =>{
            isReponseOK = false;
            alert(err.message)
        });

        if(isReponseOK){
            console.log(game);

            navigate('/game', {
                state: {
                    game: game,
                    token: token,
                    host: nickname
                }
            });
        }
    }

    // send request to start new AI game and navigate to new page
    const startNewAiGame = () =>{

    }

    const copyToken = async () =>{
        toast.success("Copied token to clipboard!")
        if ('clipboard' in navigator) {
            return await navigator.clipboard.writeText(token);
        } else {
            return document.execCommand('copy', true, token);
        }
    }

    return (
        <div>
            <div className={"container centered-absolute"}>
                <h1>{nickname}</h1>
                <h2 onClick={copyToken}>Your token: {token}</h2>
                <div className={"buttons-container"}>
                    <button onClick={startNewGame}>Start new game</button>
                    <button onClick={joinGame}>Join game</button>
                    <button onClick={startNewAiGame}>Start game with AI</button>
                </div>
            </div>
        </div>

    );
};

export default Menu;