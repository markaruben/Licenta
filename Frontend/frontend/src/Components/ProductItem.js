import React from "react";
import DeleteIcon from "@mui/icons-material/Delete";
import { useNavigate } from "react-router-dom";

function ProductItem({ id, image, name, price, onRemoveClick }) {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/Product/${id}`);
  };
  const handleRemoveClick = (event) => {
    event.stopPropagation();
    onRemoveClick(id);
  };
  return (
    <div
      className="productItem"
      onClick={handleClick}
      style={{ cursor: "pointer" }}
    >
      <div
        style={{
          backgroundImage: `url(${image})`,
          backgroundSize: "cover", // Ensure the image covers the container
          backgroundPosition: "center", // Center the image within the container
          width: "200px", // Set the width of the container
          height: "200px", // Set the height of the container
          marginTop: "10px",
        }}
        className="productImage"
      ></div>
      <h1>{name}</h1>
      <p>{price} Lei</p>
      <span
        className="remove-icon"
        style={{ cursor: "pointer" }}
        onClick={handleRemoveClick}
      >
        <DeleteIcon />
      </span>
    </div>
  );
}

export default ProductItem;
