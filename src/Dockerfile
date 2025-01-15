# Use a base image with Java
FROM openjdk:11

# Set the working directory
WORKDIR /app

# Copy the server code into the container
COPY server /app/server

# Compile the Java code
RUN javac server/ChatServer.java server/BroadcastServer.java

# Expose the port the server will run on
EXPOSE 8000

# Run the server
CMD ["java", "server.ChatServer"]
