module.exports = {
  content: ["./src/**/*.{html,ts,scss}"],
  theme: {
    extend: {
      colors: {
        app: {
          bg: "var(--color-bg)",
          surface: "var(--color-surface)",
          surfaceSoft: "var(--color-surface-soft)",
          border: "var(--color-border)",

          primary: "var(--color-primary)",
          primarySoft: "var(--color-primary-soft)",
          primaryDark: "var(--color-primary-dark)",

          accent: "var(--color-accent)",
          accentDark: "var(--color-accent-dark)",

          success: "var(--color-success)",
          warning: "var(--color-warning)",
          danger: "var(--color-danger)",

          text: "var(--color-text)",
          textMuted: "var(--color-text-muted)",
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
