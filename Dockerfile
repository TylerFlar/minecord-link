FROM eclipse-temurin:21-jdk-jammy

# Install Maven, Git, and X11 libraries
RUN apt-get update && apt-get install -y maven git libx11-6 libxext6 libxrender1 libxtst6 libxi6

# Set working directory
WORKDIR /workspaces/minecord-link

# Download and prepare Spigot
RUN mkdir -p /minecraft && cd /minecraft && \
    wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar && \
    java -jar BuildTools.jar --rev 1.21 && \
    echo "eula=true" > eula.txt && \
    echo "server-port=25565" >> server.properties

# Set up scripts to build the plugin, copy it to the server, and run the server
RUN echo '#!/bin/sh' > /usr/local/bin/build-and-run-minecraft && \
    echo 'cd /workspaces/minecord-link && mvn clean package && \
    mkdir -p /minecraft/plugins && \
    rm -f /minecraft/plugins/*.jar && \
    cp target/minecord-link-*.jar /minecraft/plugins/ && \
    ln -sf /minecraft/plugins/MineCord-Link/config.yml /workspaces/minecord-link/server-config.yml && \
    cd /minecraft && java -jar spigot-1.21.jar nogui' >> /usr/local/bin/build-and-run-minecraft && \
    chmod +x /usr/local/bin/build-and-run-minecraft

# Keep the container running
CMD ["tail", "-f", "/dev/null"]