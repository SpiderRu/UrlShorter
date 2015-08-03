
Build:

    mvn package


Run (with simple data dtorage):

    java -jar urlshorter-0.0.1.jar


Run (with Apache Cassandra storage):

    java -Dspring.profiles.active=cassandra -jar urlshorter-0.0.1.jar
    

URL:
    http://<server>:8080/
