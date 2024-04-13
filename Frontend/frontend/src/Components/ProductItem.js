import React from "react";
import DeleteIcon from "@mui/icons-material/Delete";
import { useNavigate } from "react-router-dom";

function ProductItem({ productId, image, name, price, onRemoveClick }) {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/Product/${productId}`);
  };
  const handleRemoveClick = (event) => {
    event.stopPropagation();
    onRemoveClick(productId);
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
          backgroundSize: "cover",
          backgroundPosition: "center",
          width: "200px",
          height: "200px",
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
