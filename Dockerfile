FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:op1Zm05V0kwmAIviHuJi@containers-us-west-66.railway.app:8040/railway
ENV db_user=root
ENV db_pass=op1Zm05V0kwmAIviHuJi


ENTRYPOINT ["java","-jar","demo.jar"]