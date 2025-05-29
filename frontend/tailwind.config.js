module.exports = {
  darkMode: "selector",
  content: ["./src/**/*.{html,ts,scss}"],
  theme: {
    extend: {
      colors: {
        primary: {
          50: "#f6f4f6",
          100: "#eae5eb",
          200: "#d5c9d7",
          300: "#b9a6bd",
          400: "#9e83a3",
          500: "#7e6184",
          600: "#664a6a",
          700: "#4A314D",
          800: "#3d2a40",
          900: "#2b1e2d",
          950: "#1a121b",
        },
        secondary: {
          50: "#f4f7f2",
          100: "#e8efe3",
          200: "#d2dfcb",
          300: "#b3ca9f",
          400: "#89A971",
          500: "#6c8f54",
          600: "#567243",
          700: "#455b36",
          800: "#39482d",
          900: "#2e3a25",
          950: "#171e12",
        },
      },
      boxShadow: {
        neon: "0 0 5px theme('colors.primary.500'), 0 0 20px theme('colors.purple.500')",
      },
    },
  },
  plugins: [],
};
