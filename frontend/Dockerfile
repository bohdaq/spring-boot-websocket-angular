# Step 1: Use an official Nginx image to serve the app
FROM nginx:alpine

# Step 2: Copy the built Angular app to the Nginx directory
COPY dist/frontend/browser /usr/share/nginx/html

# Step 3: Expose the default Nginx port
EXPOSE 4200

# Step 4: Start the Nginx server to serve the Angular app
CMD ["nginx", "-g", "daemon off;"]
