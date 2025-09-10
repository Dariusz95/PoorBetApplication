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
      fontSize: {
        "xs-clamp": "clamp(0.75rem, 1.5vw, 0.875rem)",
        "sm-clamp": "clamp(0.875rem, 1.8vw, 1rem)",
        "base-clamp": "clamp(1rem, 2vw, 1.125rem)",
        "lg-clamp": "clamp(1.25rem, 2.5vw, 1.5rem)",
        "xl-clamp": "clamp(1.5rem, 4vw, 2rem)",
        "2xl-clamp": "clamp(2rem, 5vw, 2.5rem)",
        "3xl-clamp": "clamp(2.5rem, 6vw, 3rem)",
      },
    },
  },
  plugins: [],
};
