import React, { useState } from "react";
import ReorderIcon from "@mui/icons-material/Reorder";
import Logo from "../Assets/app-logo.png";
import { Link } from "react-router-dom";
import "../styles/Navbar.css";
import { useNavigate } from "react-router-dom";

function Navbar() {
  const [openLinks, setOpenLinks] = useState(false);
  const navigate = useNavigate();

  const isLoggedIn = !!localStorage.getItem("jwtToken");

  const handleLogout = () => {
    localStorage.removeItem("jwtToken");
    navigate("/");
  };

  const toggleNavbar = () => {
    setOpenLinks(!openLinks);
  };

  return (
    <div className="navbar">
      <div className="leftSide " id={openLinks ? "open" : "close"}>
        <Link to="/">
          <img src={Logo} />
        </Link>
        <div className="hiddenLinks">
          <Link to="/" onClick={toggleNavbar}>
            {" "}
            Home{" "}
          </Link>
          <Link to="/Products" onClick={toggleNavbar}>
            {" "}
            Products{" "}
          </Link>
          <Link to="/About" onClick={toggleNavbar}>
            {" "}
            About&nbsp;us{" "}
          </Link>
          {isLoggedIn ? (
            <Link to="/" onClick={handleLogout}>
              Log&nbsp;Out
            </Link>
          ) : (
            <Link to="/LogIn" onClick={toggleNavbar}>
              Log&nbsp;In
            </Link>
          )}
        </div>
      </div>
      <div className="rightSide">
        <Link to="/" className="nav-link">
          {" "}
          Home{" "}
        </Link>
        <Link to="/Products" className="nav-link">
          {" "}
          Products{" "}
        </Link>
        <Link to="/About" className="nav-link">
          {" "}
          About us{" "}
        </Link>
        {isLoggedIn ? (
          <Link to="/" className="nav-link" onClick={handleLogout}>
            Log Out
          </Link>
        ) : (
          <Link to="/LogIn" className="nav-link">
            Log In
          </Link>
        )}
        <button onClick={toggleNavbar}>
          <ReorderIcon />
        </button>
      </div>
    </div>
  );
}

export default Navbar;
