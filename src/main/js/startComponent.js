import React, {useEffect, useRef} from 'react';
import {useNavigate} from "react-router-dom";
import {type} from "@testing-library/user-event/dist/type";
import toast from "react-hot-toast";

const StartComponent = () => {
    const navigate = useNavigate();

    const start = async (nickname) =>{
        if(nickname.length > 16 || nickname.length === 0){
            toast.error("Nickname length is more than 0 and less than 17")
            return null;
        }

        const response = await fetch('api/user/start',
            {
                method: "POST",
                body: nickname
            }).then(response => response.json())

        // TODO: add response handling

        navigate("/menu", {state: {nickname: nickname, token: response.token}});
    }

    return (
        <div className={"container centered-absolute"}>
            <h1>Type your nickname</h1>
            <input type={"text"} id={"nickname-input"} autoComplete={"off"}/>
            <br/>
            <button onClick={() => start(document.getElementById("nickname-input").value)}>Start</button>
        </div>
    );
};

export default StartComponent;