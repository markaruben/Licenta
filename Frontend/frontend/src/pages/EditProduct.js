import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { useSnackbar } from "notistack";
import { useNavigate } from "react-router-dom";
import { success, error, info } from "../helpers/alerts.ts";

function EditProduct() {
  const { id } = useParams(); // Extracting the id property from useParams
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();
  const [product, setProduct] = useState({
    name: "",
    description: "",
    price: 0,
    imageUrl: "",
  });

  useEffect(() => {
    const fetchProductDetails = async () => {
      try {
        const response = await fetch(
          `http://localhost:8000/products/${id}` // Using the extracted id in the URL
        );
        if (!response.ok) {
          throw new Error(`Error! status: ${response.status}`);
        }
        const productData = await response.json();
        setProduct(productData);
      } catch (error) {
        console.error("Failed to fetch product details:", error);
      }
    };

    fetchProductDetails();
  }, [id]); // Dependency on id

  const handleChange = (event) => {
    const { name, value } = event.target;
    setProduct((prevProduct) => ({
      ...prevProduct,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const response = await fetch(
        `http://localhost:8000/products/update/${id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(product),
        }
      );
      if (!response.ok) {
        throw new Error(`Error! status: ${response.status}`);
      }
      success(enqueueSnackbar, "Product updated successfully!");
    } catch (e) {
      error(enqueueSnackbar, "Failed to update product:", e);
    }
  };

  const handleDelete = async () => {
    try {
      const response = await fetch(
        `http://localhost:8000/products/delete/${id}`,
        {
          method: "DELETE",
        }
      );
      if (!response.ok) {
        throw new Error(`Error! status: ${response.status}`);
      }
      navigate("/Products");
      success(enqueueSnackbar, "Product deleted successfully!");
    } catch (e) {
      error(enqueueSnackbar, "Failed to delete product:", error);
    }
  };

  return (
    <div>
      <h2>Edit Product</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Name:
          <input
            type="text"
            name="name"
            value={product.name}
            onChange={handleChange}
            required
          />
        </label>
        <br />
        <label>
          Description:
          <input
            type="text"
            name="description"
            value={product.description}
            onChange={handleChange}
            required
          />
        </label>
        <br />
        <label>
          Price:
          <input
            type="number"
            name="price"
            value={product.price}
            onChange={handleChange}
            required
          />
        </label>
        <br />
        <label>
          Image URL:
          <input
            type="text"
            name="imageUrl"
            value={product.imageUrl}
            onChange={handleChange}
          />
        </label>
        <br />
        <button type="submit">Edit Product</button>
        <button type="button" onClick={handleDelete}>
          Delete Product
        </button>
      </form>
    </div>
  );
}

export default EditProduct;
