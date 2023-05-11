FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=build/libs/tx_user_portal-0.0.1.jar
COPY ${JAR_FILE} demo.jar

ENV db_url=jdbc:mysql://localhost:3306/txportal_test
ENV db_user=root
ENV db_pass=@S!anwa3034

ENTRYPOINT ["java","-jar","demo.jar"]