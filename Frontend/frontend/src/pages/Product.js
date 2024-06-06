import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import "../styles/Product.css";
import { useSnackbar } from "notistack";
import { success, error } from "../helpers/alerts.ts";
import { Line } from "react-chartjs-2";
import "chart.js/auto";
import "chartjs-adapter-date-fns";

function Product() {
  const { enqueueSnackbar } = useSnackbar();
  const { productId } = useParams();
  const [product, setProduct] = useState(null);
  const [userProduct, setUserProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [thresholdPrice, setThresholdPrice] = useState(0);
  const [priceHistory, setPriceHistory] = useState([]);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const jwtToken = localStorage.getItem("jwtToken");
    if (!jwtToken) {
      setLoading(false);
      return;
    }
    setIsAuthenticated(true);

    const fetchProduct = async () => {
      try {
        const response = await fetch(
          `http://192.168.1.130:8000/products/${productId}`,
          {
            headers: new Headers({
              Authorization: `Bearer ${jwtToken}`,
              "Content-Type": "application/json",
            }),
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch product details");
        }

        const data = await response.json();
        setProduct(data);
      } catch (error) {
        console.error("Error fetching product:", error);
      }
    };

    const fetchUserProduct = async (userId) => {
      try {
        const response = await fetch(
          `http://192.168.1.130:8000/products/getUserProduct/${userId}/${productId}`,
          {
            headers: new Headers({
              Authorization: `Bearer ${jwtToken}`,
              "Content-Type": "application/json",
            }),
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch user product details");
        }

        const data = await response.json();
        setUserProduct(data);
        setThresholdPrice(data.thresholdPrice);
        setLoading(false);
      } catch (error) {
        console.error("Error fetching user product:", error);
      }
    };

    const fetchPriceHistory = async () => {
      try {
        const response = await fetch(
          `http://192.168.1.130:8000/productHistory/${productId}/price-history`,
          {
            headers: new Headers({
              Authorization: `Bearer ${jwtToken}`,
              "Content-Type": "application/json",
            }),
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch price history");
        }

        const data = await response.json();

        const processedData = data.map((entry) => ({
          date: new Date(entry.date),
          price: parseFloat(entry.price),
        }));

        setPriceHistory(processedData);
      } catch (error) {
        console.error("Error fetching price history:", error);
      }
    };

    const fetchUserDetails = async () => {
      try {
        const response = await fetch(
          "http://192.168.1.130:8000/auth/user-details",
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
        return data.id;
      } catch (e) {
        console.error("There has been a problem with your fetch operation:", e);
      }
    };

    fetchProduct();
    fetchPriceHistory();
    fetchUserDetails().then((userId) => {
      if (userId) {
        fetchUserProduct(userId);
      }
    });
  }, [productId]);

  const handleThresholdPriceChange = (event) => {
    setThresholdPrice(event.target.value);
  };

  const updateThresholdPrice = async () => {
    try {
      const jwtToken = localStorage.getItem("jwtToken");
      const response = await fetch(
        `http://192.168.1.130:8000/products/${userProduct.id}/threshold-price`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwtToken}`,
          },
          body: JSON.stringify({ thresholdPrice }),
        }
      );

      if (!response.ok) {
        throw new Error("Failed to update threshold price");
      }
      success(enqueueSnackbar, "Threshold price updated successfully");
    } catch (e) {
      error(enqueueSnackbar, "Error updating threshold price:" + e);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="loggedout-container">
        Please log in to view this page.
      </div>
    );
  }

  if (loading || !product || !userProduct) {
    return <div className="loading">Loading...</div>;
  }

  const data = {
    labels: priceHistory.map((entry) => entry.date),
    datasets: [
      {
        label: "Price",
        data: priceHistory.map((entry) => ({
          x: entry.date,
          y: entry.price,
        })),
        borderColor: "rgba(75, 192, 192, 1)",
        fill: false,
      },
    ],
  };

  const options = {
    scales: {
      x: {
        type: "time",
        time: {
          unit: "day",
        },
      },
      y: {
        beginAtZero: true,
      },
    },
  };

  return (
    <div className="product">
      <h2 className="product-title">{product.title}</h2>
      <div className="product-content">
        <div className="product-image">
          <img src={product.imageUrl} alt={product.title} />
        </div>
        <div className="product-details">
          <p className="product-price">Price: {product.price} Lei</p>
          <p className="product-threshold">
            Threshold Price:{" "}
            <input
              type="number"
              value={thresholdPrice}
              onChange={handleThresholdPriceChange}
              className="threshold-input"
            />{" "}
            Lei
          </p>
          <button className="product-button" onClick={updateThresholdPrice}>
            Update Threshold Price
          </button>
        </div>
        <div className="chart-container">
          <Line data={data} options={options} />
        </div>
      </div>
    </div>
  );
}

export default Product;
