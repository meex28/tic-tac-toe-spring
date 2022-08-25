import React from 'react';
// import './main.css'

const GameBoard = ({board, handleClick}) => {
    return (
        <div className={"board"}>
            {[...Array(9)].map((x, i) =>{
                return(<div className={"field"} id={"field-"+i}
                            onClick={(event) => handleClick(event.target.id.slice(-1))}>
                    {board[i]}
                    </div>)
            })}
        </div>
    );
};

export default GameBoard;