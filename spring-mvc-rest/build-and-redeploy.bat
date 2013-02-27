call mvn clean install

rm -r -f C:\Java\apache-tomcat-7.0.29\webapps\spring-mvc-rest
rm -f C:\Java\apache-tomcat-7.0.29\webapps\spring-mvc-rest.war

cd target
cp spring-mvc-rest.war C:\Java\apache-tomcat-7.0.29\webapps\
cd ..

rem call C:\Java\apache-tomcat-7.0.29\bin\shutdown.bat

rem C:\Java\apache-tomcat-7.0.29\bin\catalina jpda start