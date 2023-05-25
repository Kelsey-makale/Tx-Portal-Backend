FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:SYzejhze3YLOjAIZ1A2Q@containers-us-west-202.railway.app:7154/railway
ENV db_user=root
ENV db_pass=SYzejhze3YLOjAIZ1A2Q


ENTRYPOINT ["java","-jar","demo.jar"]