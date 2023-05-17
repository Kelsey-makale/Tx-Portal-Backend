FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:mhKkZuxKaFgU7wZiNxa8@containers-us-west-32.railway.app:7890/railway
ENV db_user=root
ENV db_pass=mhKkZuxKaFgU7wZiNxa8


ENTRYPOINT ["java","-jar","demo.jar"]