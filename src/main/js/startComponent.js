import React, {useEffect, useRef} from 'react';
import {useNavigate} from "react-router-dom";
import {type} from "@testing-library/user-event/dist/type";

const StartComponent = () => {
    const navigate = useNavigate();

    const start = async (nickname) =>{
        // let token = "token"

        const response = await fetch('api/user/start',
            {
                method: "POST",
                body: nickname
            }).then(response => response.json())

        // TODO: add response handling

        navigate("/menu", {state: {nickname: nickname, token: response.token}});
    }

    return (
        <div>
            <h1>Type your nickname</h1>
            <input type={"text"} id={"nickname-input"}/>
            <br/>
            <button onClick={() => start(document.getElementById("nickname-input").value)}>Start</button>
        </div>
    );
};

export default StartComponent;