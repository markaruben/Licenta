import React from "react";
import { Link } from "react-router-dom";
import "../styles/Home.css";

function Home() {
  return (
    <div
      className="home"
      style={{
        backgroundImage: `url(https://images.unsplash.com/photo-1594969155368-f19485a9d88c?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3Dhttps://images.unsplash.com/photo-1545535408-2b4d520cbd88?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D)`,
      }}
    >
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
