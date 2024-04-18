import React, { useState, useEffect } from "react";
import "../styles/FavoriteProduct.css";
import { enqueueSnackbar, useSnackbar } from "notistack";
import { success, error, info } from "../helpers/alerts.ts";

function FavoriteProduct() {
  const [productUrl, setProductUrl] = useState("");
  const [thresholdPrice, setThresholdPrice] = useState("");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userId, setUserId] = useState(null);

  useEffect(() => {
    const jwtToken = localStorage.getItem("jwtToken");
    if (jwtToken) {
      setIsLoggedIn(true);
      fetchUserDetails(jwtToken);
    }
  }, []);

  const fetchUserDetails = async (jwtToken) => {
    try {
      const response = await fetch("http://localhost:8000/auth/user-details", {
        method: "GET",
        headers: new Headers({
          Authorization: `Bearer ${jwtToken}`,
          "Content-Type": "application/json",
        }),
      });
      if (!response.ok) throw new Error("Network response was not ok.");
      const data = await response.json();
      // Convert userId to an integer
      setUserId(data.id);
    } catch (error) {
      console.error(
        "There has been a problem with your fetch operation:",
        error
      );
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!userId) {
      console.error("User ID not available");
      return;
    }

    const requestBody = {
      productUrl: productUrl,
      thresholdPrice: thresholdPrice,
    };

    try {
      const response = await fetch(
        `http://localhost:8000/products/addProduct/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestBody),
        }
      );

      const responseData = await response.text();
      if (!response.ok) {
        error(enqueueSnackbar, responseData);
        return;
      }
      success(enqueueSnackbar, "Product added to favorites!");
    } catch (e) {
      error(enqueueSnackbar, "Error: " + e.message);
    }
  };

  const handleLogin = () => {
    window.location.href = "/login";
  };

  return (
    <div className={isLoggedIn ? "container" : "logged-out-container"}>
      <h2>Add Favorite Product</h2>
      {isLoggedIn ? (
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="productUrl">Product URL:</label>
            <input
              type="text"
              id="productUrl"
              value={productUrl}
              onChange={(e) => setProductUrl(e.target.value)}
              required
              className="favprod-input"
            />
          </div>
          <div className="form-group">
            <label htmlFor="thresholdPrice">Threshold Price:</label>
            <input
              type="number"
              id="thresholdPrice"
              value={thresholdPrice}
              onChange={(e) => setThresholdPrice(e.target.value)}
              required
              className="favprod-input"
            />
          </div>
          <button type="submit">Submit</button>
        </form>
      ) : (
        <div className="logged-out-content">
          <p>Please log in to add a favorite product.</p>
          <button className="log-in-btn" onClick={handleLogin}>
            Log In
          </button>
        </div>
      )}
    </div>
  );
}

export default FavoriteProduct;
