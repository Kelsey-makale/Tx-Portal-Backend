FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:B52ta6q7sebJO6jbhiYB@containers-us-west-108.railway.app:6373/railway
ENV db_user=root
ENV db_pass=B52ta6q7sebJO6jbhiYB


ENTRYPOINT ["java","-jar","demo.jar"]