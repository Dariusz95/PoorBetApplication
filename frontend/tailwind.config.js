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
        sm: "var(--radius-sm)",
        md: "var(--radius-md)",
        lg: "var(--radius-lg)",
        xl: "var(--radius-xl)",
      },

      transitionDuration: {
        fast: "var(--duration-fast)",
        base: "var(--duration-base)",
        slow: "var(--duration-slow)",
      },

      transitionTimingFunction: {
        standard: "var(--ease-standard)",
        "out-back": "var(--ease-out-back)",
      },

      boxShadow: {
        surface: "var(--shadow-surface)",
        glow: "var(--shadow-glow)",
        elevated: "var(--shadow-elevated)",
        "glow-sm": "var(--shadow-glow-sm)",
        "glow-lg": "var(--shadow-glow-lg)",
        "accent-glow": "var(--shadow-accent-glow)",
        "success-glow": "var(--shadow-success-glow)",
        "danger-glow": "var(--shadow-danger-glow)",
      },

      backgroundImage: {
        "app-gradient": "var(--gradient-app)",
      },

      fontFamily: {
        sans: ["Inter", "system-ui", "sans-serif"],
        display: ["Inter", "system-ui", "sans-serif"],
      },
    },
  },
  plugins: [],
};
