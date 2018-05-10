FROM java:8
MAINTAINER Guillaume Bailleul <gbm.bailleul@gmail.com>

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/myservice/myservice.jar"]

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
#ADD target/lib           /usr/share/myservice/lib
# Add the service itself
#ARG JAR_FILE
ADD target/xemph-service-1.0-SNAPSHOT.jar /usr/share/myservice/myservice.jar