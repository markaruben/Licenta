import React from "react";

function Contact() {
  return (
    <div className="contact">
      <div
        className="leftSide"
        style={{
          backgroundImage: `url({https://images.unsplash.com/photo-1499159058454-75067059248a?q=80&w=2071&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D})`,
        }}
      ></div>
      <div className="rightSide">
        <h1>Contact us!</h1>
      </div>
    </div>
  );
}

export default Contact;
