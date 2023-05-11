FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:y5icBuigN3gVNBsUJOQk@containers-us-west-181.railway.app:6043/railway
ENV db_user=root
ENV db_pass=y5icBuigN3gVNBsUJOQk


ENTRYPOINT ["java","-jar","demo.jar"]