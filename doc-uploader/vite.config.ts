import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const isProduction = process.env.NODE_ENV === 'production';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: isProduction
          ? 'https://twitter-clone-xkw3.onrender.com'
          : 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
});