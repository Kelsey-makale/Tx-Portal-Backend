FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:jE5LMttIS2PLCihLHuEm@containers-us-west-47.railway.app:6819/railway
ENV db_user=root
ENV db_pass=jE5LMttIS2PLCihLHuEm


ENTRYPOINT ["java","-jar","demo.jar"]