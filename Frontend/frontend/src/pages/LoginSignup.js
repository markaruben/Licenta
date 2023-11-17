import React, { useState } from "react";
import "../styles/LoginSignup.css";
import userIcon from "../Assets/person.png";
import emailIcon from "../Assets/email.png";
import passwordIcon from "../Assets/password.png";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import VisibilityIcon from "@mui/icons-material/Visibility";

const LoginSignup = () => {
  const [action, setAction] = useState("Log In");
  const [showIcon, setShowIcon] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [password, setPassword] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState("");

  const toggleShowPassword = () => {
    setShowPassword(!showPassword);
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
  };

  const submitAction = () => {
    if (action === "Log In") {
      setAction("Sign Up");
    } else {
      setAction("Log In");
    }

    setPassword("");
    setPasswordError("");
    setShowIcon(false);
    setShowPassword(false);
    setEmail("");
    setEmailError("");
  };

  const validatePassword = () => {
    const passwordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}$/;
    if (!passwordRegex.test(password)) {
      setPasswordError(
        "Password must contain at least 6 characters, including one uppercase letter, one lowercase letter, and one digit."
      );
    } else {
      setPasswordError("");
    }
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
  };

  const validateEmail = () => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setEmailError("Please enter a valid email address.");
    } else {
      setEmailError("");
    }
  };

  return (
    <div className="container">
      <div className="header">
        <div className="text">{action}</div>
        <div className="underline"></div>
      </div>
      <div className="inputs">
        {action === "Log In" ? (
          <div></div>
        ) : (
          <div className="input">
            <img src={userIcon} alt="" />
            <input type="text" placeholder="Name" />
          </div>
        )}
        {action === "Log In" ? (
          <div className="input">
            <img src={emailIcon} alt="" />
            <input
              type="email"
              onChange={handleEmailChange}
              value={email}
              placeholder="Email"
            />
          </div>
        ) : (
          <div className="input">
            <img src={emailIcon} alt="" />
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={handleEmailChange}
              onBlur={validateEmail}
            />
          </div>
        )}
        {action === "Log In" ? (
          <div className="input">
            <img src={passwordIcon} alt="" />
            {
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Password"
                value={password}
                onChange={handlePasswordChange}
              />
            }
            {showIcon === false ? (
              <span
                onClick={() => {
                  setShowIcon(true);
                  toggleShowPassword();
                }}
              >
                <VisibilityOffIcon />
              </span>
            ) : (
              <span
                onClick={() => {
                  setShowIcon(false);
                  toggleShowPassword();
                }}
              >
                <VisibilityIcon />
              </span>
            )}
          </div>
        ) : (
          <div className="input">
            <img src={passwordIcon} alt="" />
            {
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Password"
                value={password}
                onChange={handlePasswordChange}
                onBlur={validatePassword}
              />
            }
            {showIcon === false ? (
              <span
                onClick={() => {
                  setShowIcon(true);
                  toggleShowPassword();
                }}
              >
                <VisibilityOffIcon />
              </span>
            ) : (
              <span
                onClick={() => {
                  setShowIcon(false);
                  toggleShowPassword();
                }}
              >
                <VisibilityIcon />
              </span>
            )}
          </div>
        )}
        {emailError && action === "Sign Up" && (
          <p className="error">{emailError}</p>
        )}
        {passwordError && action === "Sign Up" && (
          <p className="error">{passwordError}</p>
        )}
      </div>
      {action === "Sign Up" ? (
        <div></div>
      ) : (
        <div className="forgot-password">
          Forgot Passowrd? <span>Click Here!</span>
        </div>
      )}
      <div className="submit-container">
        <div
          className={action === "Log In" ? "submit gray" : "submit"}
          onClick={() => {
            submitAction();
          }}
        >
          Sign Up
        </div>
        <div
          className={action === "Sign Up" ? "submit gray" : "submit"}
          onClick={() => {
            submitAction();
          }}
        >
          Log In
        </div>
      </div>
    </div>
  );
};

export default LoginSignup;
