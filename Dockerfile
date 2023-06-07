FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:iFLCIKlYdryh8Qt9VVrH@containers-us-west-74.railway.app:7557/railway
ENV db_user=root
ENV db_pass=iFLCIKlYdryh8Qt9VVrH


ENTRYPOINT ["java","-jar","demo.jar"]