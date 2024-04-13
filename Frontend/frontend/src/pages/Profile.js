import React, { useEffect, useState } from "react";
import "../styles/Profile.css";
import { useNavigate } from "react-router-dom";
import ProductItem from "../Components/ProductItem";

function Profile() {
  const [userDetails, setUserDetails] = useState("");
  const [favoriteProducts, setFavoriteProducts] = useState([]);
  const [isAuthenticated, setIsAuthenticated] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const jwtToken = localStorage.getItem("jwtToken");
    if (!jwtToken) {
      setIsAuthenticated(false);
      return;
    }
    setIsAuthenticated(true);

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

        setUserDetails(data);
        console.log(userDetails);

        return data.id;
      } catch (e) {
        console.error("There has been a problem with your fetch operation:", e);
      }
    };

    const fetchFavoriteProducts = async (userId) => {
      try {
        const response = await fetch(
          `http://localhost:8000/products/user/${userId}/favorite-products`,
          {
            method: "GET",
            headers: new Headers({
              Authorization: `Bearer ${jwtToken}`,
              "Content-Type": "application/json",
            }),
          }
        );
        if (!response.ok) throw new Error("Failed to fetch favorite products.");
        const favorites = await response.json();

        setFavoriteProducts(favorites);
        console.log(favorites);
      } catch (error) {
        console.error("Error fetching favorite products:", error);
      }
    };

    fetchUserDetails().then((userId) => {
      if (userId) {
        fetchFavoriteProducts(userId);
      }
    });
  }, [navigate]);

  const removeFromFavorites = async (productId) => {
    const jwtToken = localStorage.getItem("jwtToken");
    if (!jwtToken) {
      console.error("JWT token not found");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8000/products/removeFavorite/${userDetails.id}/${productId}`,
        {
          method: "DELETE",
          headers: new Headers({
            Authorization: `Bearer ${jwtToken}`,
            "Content-Type": "application/json",
          }),
        }
      );

      if (!response.ok) {
        throw new Error("Failed to remove the product from favorites.");
      }
      setFavoriteProducts(
        favoriteProducts.filter((product) => product.id !== productId)
      );
    } catch (error) {
      console.error("Error removing product from favorites:", error);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="login-container">
        <p className="login-message">Please log in!</p>
        <button className="logButton" onClick={() => navigate("/login")}>
          Log In
        </button>
      </div>
    );
  }

  return (
    <div className="profile-page">
      <div className="profile-container">
        <h2 className="profile-title">User Details</h2>
        <p className="user-detail">Name: {userDetails.username}</p>
        <p className="user-detail">Email: {userDetails.email}</p>
      </div>
      <div className="favorites-container">
        <h3>Favorite Products</h3>
        <div className="product-list">
          {favoriteProducts && favoriteProducts.length > 0 ? (
            favoriteProducts.map((product, index) => (
              <ProductItem
                key={product.id}
                productId={product.id}
                image={product.imageUrl}
                name={product.name}
                price={product.price}
                isFavorited={true}
                onRemoveClick={() => removeFromFavorites(product.id)}
              />
            ))
          ) : (
            <p className="no-products">No favorite products.</p>
          )}
        </div>
      </div>
    </div>
  );
}

export default Profile;
