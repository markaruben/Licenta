import React, { useState, useEffect } from "react";
import ProductItem from "../Components/ProductItem";
import "../styles/Products.css";
import { useNavigate } from "react-router-dom";

function Products() {
  const [userDetails, setUserDetails] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [products, setProducts] = useState([]);
  const [favoriteProducts, setFavoriteProducts] = useState([]);
  const navigate = useNavigate();

  const handleSearchChange = (event) => {
    setSearchQuery(event.target.value);
  };

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await fetch("http://localhost:8000/products/fetchAll");
        if (!response.ok) {
          throw new Error(`Error! status: ${response.status}`);
        }
        const result = await response.json();
        setProducts(result);
      } catch (error) {
        console.log("Failed to fetch products:", error);
      }
    };

    const fetchUserDetails = async () => {
      const jwtToken = localStorage.getItem("jwtToken");
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

        return data.id;
      } catch (error) {
        console.error(
          "There has been a problem with your fetch operation:",
          error
        );
      }
    };

    const fetchFavoriteProducts = async (userId) => {
      const jwtToken = localStorage.getItem("jwtToken");
      try {
        const response = await fetch(
          `http://localhost:8000/user/${userId}/favorites`,
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
      } catch (error) {
        console.error("Error fetching favorite products:", error);
      }
    };

    fetchProducts();
    if (localStorage.getItem("jwtToken")) {
      fetchUserDetails().then((userId) => {
        if (userId) {
          fetchFavoriteProducts(userId);
        }
      });
    }
  }, []);

  const filteredProducts = products.filter((productItem) =>
    productItem.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const isProductFavorited = (productId) => {
    return favoriteProducts.some((product) => product.id === productId);
  };

  const toggleFavorite = async (productId, isFavorited) => {
    const jwtToken = localStorage.getItem("jwtToken");
    const urlBase = `http://localhost:8000/user/${userDetails.id}/favorites`;
    const method = isFavorited ? "DELETE" : "POST";
    const url = `${urlBase}/${productId}`; // Use productId in the URL

    try {
      const response = await fetch(url, {
        method,
        headers: new Headers({
          Authorization: `Bearer ${jwtToken}`,
          "Content-Type": "application/json",
        }),
        ...(method === "POST" && { body: JSON.stringify({ productId }) }),
      });

      if (!response.ok) throw new Error("Failed to toggle favorite status.");

      setFavoriteProducts((currentFavorites) =>
        isFavorited
          ? currentFavorites.filter((p) => p.id !== productId)
          : [...currentFavorites, products.find((p) => p.id === productId)]
      );
    } catch (error) {
      console.error("Error toggling favorite status:", error);
    }
  };

  const isLoggedIn = !!localStorage.getItem("jwtToken");

  return (
    <div className="products">
      <h1 className="productsTitle">Pick your favourite products!</h1>

      {!isLoggedIn ? (
        <div className="loginPrompt">
          <p>Please, log in to pick your products!</p>
          <button onClick={() => navigate("/LogIn")}>Log In</button>
        </div>
      ) : (
        <>
          <div className="searchBar">
            <input
              type="text"
              placeholder="Search products..."
              value={searchQuery}
              onChange={handleSearchChange}
            />
          </div>

          {/* Corrected product list rendering */}
          <div className="productList">
            {filteredProducts.map((productItem) => (
              <div key={productItem.id}>
                {" "}
                {/* Use productItem.id for key */}
                <ProductItem
                  image={productItem.image}
                  name={productItem.name}
                  price={productItem.price}
                  isFavorited={isProductFavorited(productItem.id)}
                  onFavClick={() =>
                    toggleFavorite(
                      productItem.id,
                      isProductFavorited(productItem.id)
                    )
                  }
                />
              </div>
            ))}
          </div>
        </>
      )}
      <button className="addProdButton" onClick={() => navigate("/AddProduct")}>
        Add Product
      </button>
    </div>
  );
}

export default Products;
