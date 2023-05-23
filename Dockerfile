FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:7rIXZ8dnBIY1XkoxLjHS@containers-us-west-196.railway.app:7666/railway
ENV db_user=root
ENV db_pass=7rIXZ8dnBIY1XkoxLjHS


ENTRYPOINT ["java","-jar","demo.jar"]