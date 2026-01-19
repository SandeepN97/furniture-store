/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f0f9ff',
          100: '#e0f2fe',
          200: '#bae6fd',
          300: '#7dd3fc',
          400: '#38bdf8',
          500: '#0ea5e9',
          600: '#0284c7',
          700: '#0369a1',
          800: '#075985',
          900: '#0c4a6e',
        },
      },
      fontSize: {
        'touch': '1.125rem', // 18px for easy touch
        'touch-lg': '1.5rem', // 24px for large buttons
      },
      spacing: {
        'touch': '3.5rem', // 56px minimum touch target
      },
    },
  },
  plugins: [],
}
