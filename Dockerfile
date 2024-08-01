FROM eclipse-temurin:21-jdk-jammy

# Install Maven and Git
RUN apt-get update && apt-get install -y maven git

# Set working directory
WORKDIR /workspaces/minecord-link

# Download and prepare Spigot
RUN mkdir -p /minecraft && cd /minecraft && \
    wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar && \
    java -jar BuildTools.jar --rev 1.21 && \
    echo "eula=true" > eula.txt && \
    echo "server-port=25565" >> server.properties

# Set up a script to run the server
RUN echo '#!/bin/sh\ncd /minecraft && java -jar spigot-1.21.jar nogui' > /usr/local/bin/run-minecraft && \
    chmod +x /usr/local/bin/run-minecraft

# Keep the container running
CMD ["tail", "-f", "/dev/null"]