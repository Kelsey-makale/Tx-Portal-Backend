FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://root:EhgAhdbT8wT5wdjbatPH@containers-us-west-90.railway.app:5808/railway
ENV db_user=root
ENV db_pass=EhgAhdbT8wT5wdjbatPH


ENTRYPOINT ["java","-jar","demo.jar"]