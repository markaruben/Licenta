import React from "react";
import { Link } from "react-router-dom";
import "../styles/Home.css";

function Home() {
  return (
    <div className="home">
      <div className="headerContainer">
        <h1>Welcome to DealTrackr</h1>
        <p>Start Saving on Your Favorite Products!</p>
        <Link to="/FavoriteProduct">
          <button>Start Tracking</button>
        </Link>
      </div>
    </div>
  );
}

export default Home;
