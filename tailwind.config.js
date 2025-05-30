/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/main/resources/templates/**/*.html"],
  theme: {
    extend: {
      colors: {
        primary: "#3b82f6",
        secondary: "#1d4ed8",
        fluid1: "#a7f3d0",
        fluid2: "#67e8f9",
      },
      fontFamily: {
        sans: [
          "Inter",
          "Noto Sans JP",
          "system-ui",
          "-apple-system",
          "BlinkMacSystemFont",
          "sans-serif",
        ],
      },
    },
  },
  plugins: [require("daisyui")],
  daisyui: {
    themes: ["light", "dark"],
  },
};
