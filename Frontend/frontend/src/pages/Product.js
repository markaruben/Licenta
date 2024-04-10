import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import "../styles/Product.css";

function Product() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    console.log(id);
    const fetchProduct = async () => {
      try {
        const response = await fetch(`http://localhost:8000/products/${id}`, {
          headers: {
            "Content-Type": "application/json",
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch product details");
        }

        const data = await response.json();
        setProduct(data);
        setLoading(false);
      } catch (error) {
        console.error("Error fetching product:", error);
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!product) {
    return <div>Product not found</div>;
  }

  return (
    <div className="product">
      <h2 className="product-title">{product.title}</h2>
      <div className="product-image">
        <img src={product.imageUrl} alt={product.title} />
      </div>
      <div className="product-details">
        <p>Price: {product.price} Lei</p>
      </div>
    </div>
  );
}

export default Product;
