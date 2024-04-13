import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import "../styles/Product.css";
import { useSnackbar } from "notistack";
import { success, error, info } from "../helpers/alerts.ts";

function Product() {
  const { enqueueSnackbar } = useSnackbar();
  const { productId } = useParams();
  const [product, setProduct] = useState(null);
  const [userProduct, setUserProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [thresholdPrice, setThresholdPrice] = useState(0);
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
          `http://localhost:8000/products/${productId}`,
          {
            headers: {
              "Content-Type": "application/json",
            },
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
          `http://localhost:8000/products/getUserProduct/${userId}/${productId}`,
          {
            headers: {
              "Content-Type": "application/json",
            },
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

    const fetchUserDetails = async () => {
      try {
        const response = await fetch(
          "http://localhost:8000/auth/user-details",
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
        `http://localhost:8000/products/${userProduct.id}/threshold-price`,
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
    return <div>Please log in to view this page.</div>;
  }

  if (loading || !product || !userProduct) {
    return <div className="loading">Loading...</div>;
  }

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
            />{" "}
            Lei
          </p>
          <button className="product-button" onClick={updateThresholdPrice}>
            Update Threshold Price
          </button>
        </div>
      </div>
    </div>
  );
}

export default Product;
