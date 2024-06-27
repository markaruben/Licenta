import React, { useState, useEffect } from "react";
import "../styles/FavoriteProduct.css";
import { enqueueSnackbar, useSnackbar } from "notistack";
import { success, error, info } from "../helpers/alerts.ts";

function FavoriteProduct() {
  const [productUrl, setProductUrl] = useState("");
  const [thresholdPrice, setThresholdPrice] = useState("");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userId, setUserId] = useState();

  useEffect(() => {
    const jwtToken = localStorage.getItem("jwtToken");
    if (jwtToken) {
      setIsLoggedIn(true);
      fetchUserDetails(jwtToken);
    }
  }, []);

  const fetchUserDetails = async (jwtToken) => {
    try {
      const response = await fetch(
        "http://192.168.0.119:8000/auth/user-details",
        {
          method: "GET",
          headers: new Headers({
            Authorization: `Bearer ${jwtToken}`,
            "Content-Type": "application/json",
          }),
        }
      );
      if (!response.ok) throw new Error("Network response was not ok.");
      const data = await response.json();
      setUserId(parseInt(data.id));
      console.log(userId);
    } catch (error) {
      console.error(
        "There has been a problem with your fetch operation:",
        error
      );
    }
  };

  const isValidUrl = (url) => {
    const allowedDomains = ["emag.ro", "amazonshop.ro"];
    try {
      const { hostname } = new URL(url);
      return allowedDomains.some((domain) => hostname.includes(domain));
    } catch (e) {
      return false;
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!userId) {
      console.error("User ID not available");
      return;
    }

    if (!isValidUrl(productUrl)) {
      info(
        enqueueSnackbar,
        "Only URLs from emag.ro or amazonshop.ro are allowed."
      );
      return;
    }

    const requestBody = {
      productUrl: productUrl,
      thresholdPrice: thresholdPrice,
    };

    try {
      const jwtToken = localStorage.getItem("jwtToken");
      const response = await fetch(
        `http://192.168.0.119:8000/products/addProduct/${userId}`,
        {
          method: "POST",
          headers: new Headers({
            Authorization: `Bearer ${jwtToken}`,
            "Content-Type": "application/json",
          }),
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
    <div className="main-content">
      <div className={isLoggedIn ? "container" : "logged-out-container"}>
        {isLoggedIn ? (
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <h2 className="title">Add your favorite product </h2>
              <p className="warning">
                Please only insert URLs from eMAG or AmazonShop
              </p>
              <label htmlFor="productUrl">
                Enter the URL for your product:
              </label>
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
    </div>
  );
}

export default FavoriteProduct;
