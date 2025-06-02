/** @type {import('tailwindcss').Config} */
export default {
  content: ["./src/**/*.{html,js,ts,jsx,tsx}", "./src/main/resources/templates/**/*.html"],
  theme: {
    extend: {},
  },
  plugins: [require("daisyui")],
};
