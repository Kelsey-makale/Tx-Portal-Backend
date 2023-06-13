FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:oLVdwAuMwva1ETPxe8P8@containers-us-west-70.railway.app:7427/railway
ENV db_user=root
ENV db_pass=oLVdwAuMwva1ETPxe8P8


ENTRYPOINT ["java","-jar","demo.jar"]