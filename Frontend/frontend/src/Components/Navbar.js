import React, { useState } from "react";
import ReorderIcon from "@mui/icons-material/Reorder";
import Logo from "../Assets/app-logo.png";
import { Link } from "react-router-dom";
import "../styles/Navbar.css";

function Navbar() {
  const [openLinks, setOpenLinks] = useState(false);

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
          <Link to="/contact" onClick={toggleNavbar}>
            {" "}
            Contact{" "}
          </Link>
          <Link to="/LogIn" onClick={toggleNavbar}>
            {" "}
            Log&nbsp;in
          </Link>
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
        <Link to="/contact" className="nav-link">
          {" "}
          Contact{" "}
        </Link>
        <Link to="/LogIn" className="nav-link">
          {" "}
          Log in
        </Link>
        <button onClick={toggleNavbar}>
          <ReorderIcon />
        </button>
      </div>
    </div>
  );
}

export default Navbar;
