module.exports = {
  content: ["./src/**/*.{html,ts,scss}"],
  theme: {
    extend: {
      colors: {
        app: {
          bg: "#0e1117",
          surface: "#1e222b",
          surfaceSoft: "#252b38",
          border: "rgba(255,255,255,0.06)",

          primary: "#8B5CF6",
          primaryDark: "#6D28D9",

          accent: "#3B82F6",
          accentDark: "#2563EB",

          success: "#22C55E",
          danger: "#e16f67",

          text: "#E5E7EB",
          textMuted: "#9CA3AF",
        },
      },

      borderRadius: {
        sm: "4px",
        md: "8px",
        lg: "16px",
        xl: "24px",
      },

      boxShadow: {
        surface: "0 10px 30px rgba(0,0,0,0.6)",
        glow: "0 0 30px rgba(139,92,246,0.35)",
        elevated: "0 20px 50px rgba(0,0,0,0.7)",
      },

      backgroundImage: {
        "app-gradient": `
          radial-gradient(circle at 100% 0%, rgba(139,92,246,0.25) 0%, transparent 45%),
          radial-gradient(circle at 0% 100%, rgba(59,130,246,0.15) 0%, transparent 50%),
          linear-gradient(180deg, #151922 0%, #0e1117 100%)
        `,
      },

      fontFamily: {
        sans: ["Inter", "system-ui", "sans-serif"],
        display: ["Inter", "system-ui", "sans-serif"],
      },
    },
  },
  plugins: [],
};
