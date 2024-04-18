import React, { useState } from "react";
import "../styles/LoginSignup.css";
import userIcon from "../Assets/person.png";
import emailIcon from "../Assets/email.png";
import passwordIcon from "../Assets/password.png";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { useNavigate } from "react-router-dom";
import { useSnackbar } from "notistack";
import { success, error, info } from "../helpers/alerts.ts";

const LoginSignup = () => {
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();
  const [action, setAction] = useState("Log In");
  const [showIcon, setShowIcon] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [password, setPassword] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState("");
  const [name, setName] = useState("");

  const toggleShowPassword = () => {
    setShowPassword(!showPassword);
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
  };

  const toggleForm = () => {
    setAction(action === "Log In" ? "Sign Up" : "Log In");
    setName("");
    setPassword("");
    setPasswordError("");
    setShowIcon(false);
    setShowPassword(false);
    setEmail("");
    setEmailError("");
  };

  const handleLogin = async () => {
    try {
      const response = await fetch("http://localhost:8000/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username: name, password: password }),
      });
      if (response.ok) {
        const data = await response.json();
        success(enqueueSnackbar, "Welcome " + name);
        const token = data.jwt;
        localStorage.setItem("jwtToken", token);
        navigate("/Profile");
      } else {
        error(enqueueSnackbar, "Login failed");
      }
    } catch (e) {
      error(enqueueSnackbar, "Login error:", e);
    }
  };

  const handleRegister = async () => {
    try {
      const response = await fetch("http://localhost:8000/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: name,
          email: email,
          password: password,
        }),
      });
      if (response.status === 201 || response.status === 200) {
        const data = await response.json();
        success(
          enqueueSnackbar,
          "Registered Successfully! Welcome, " + name + "!"
        );
        toggleForm();
      } else {
        error(enqueueSnackbar, "Registration failed");
      }
    } catch (e) {
      error(enqueueSnackbar, "Registration error:" + e);
    }
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
    <div className="ls-container">
      <div className="ls-header">
        <div className="ls-text">{action}</div>
        <div className="ls-underline"></div>
      </div>
      <div className="ls-inputs">
        <div className="ls-input-login">
          <img src={userIcon} alt="" />
          <input
            type="text"
            placeholder="Username"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>
        {action === "Log In" ? (
          <div></div>
        ) : (
          <div>
            <div className="ls-input-login">
              <img src={emailIcon} alt="" />
              <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={handleEmailChange}
                onBlur={validateEmail}
              />
            </div>
          </div>
        )}

        {action === "Log In" ? (
          <div className="ls-input-login">
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
          <div className="ls-input-login">
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
          <p className="ls-error">{emailError}</p>
        )}
        {passwordError && action === "Sign Up" && (
          <p className="ls-error">{passwordError}</p>
        )}
      </div>
      <div className="ls-submit-container">
        <div
          className="ls-submit"
          onClick={() => {
            action === "Log In" ? handleLogin() : handleRegister();
          }}
        >
          {action}
        </div>
      </div>
      <div className="ls-toggle-action">
        {action === "Log In" ? (
          <p>
            Don't have an account? <span onClick={toggleForm}>Sign Up</span>
          </p>
        ) : (
          <p>
            Already have an account? <span onClick={toggleForm}>Sign In</span>
          </p>
        )}
      </div>
    </div>
  );
};

export default LoginSignup;
