# Use a base image with Java
FROM openjdk:23

# Set the working directory
WORKDIR /app

# Copy the server code into the container
COPY server /app/server

# Compile the Java code
RUN javac server/ChatServer.java server/BroadcastServer.java

# Expose the necessary ports
EXPOSE 8000 8001 8002

# Run the server
CMD ["java", "server.ChatServer"]

# docker run -d --name chat-server -p 8000:8000 -p 8001:8001/udp -p 8002:8002 chat-server