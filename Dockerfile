FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:FfTTXM3jH9Y0KaGDxMlr@containers-us-west-132.railway.app:6504/railway
ENV db_user=root
ENV db_pass=FfTTXM3jH9Y0KaGDxMlr


ENTRYPOINT ["java","-jar","demo.jar"]