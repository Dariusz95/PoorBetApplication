module.exports = {
  "/api/**": {
    target: "https://localhost:8080/api",
    secure: false,
    changeOrigin: true,
  },
};
