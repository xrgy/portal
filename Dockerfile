FROM centos


VOLUME /tmp

ADD jdk-8u191-linux-x64.tar.gz /usr/local/
#ADD apache-tomcat-8.5.34.tar.gz /usr/local/

ENV JAVA_HOME /usr/local/jdk1.8.0_191
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH $PATH:$JAVA_HOME/bin
#ENV CATALINA_HOME /usr/local/apache-tomcat-8.5.34
#ENV CATALINA_BASE /usr/local/apache-tomcat-8.5.34
#ENV PATH $PATH:$JAVA_HOME/bin:$CATALINA_HOME/lib:$CATALINA_HOME/bin
#ADD portal-1.0.war /usr/local/apache-tomcat-8.5.34/webapps
COPY lib /lib
ADD portal-1.0.jar /portal.jar
#RUN apt-get -y install wget git unzip
#RUN unzip portal-1.0.jar -d /portal

EXPOSE 8082
#CMD /usr/local/apache-tomcat-8.5.34/bin/catalina.sh run
#CMD ["--spring.config.location=/config/application.properties"] jar包没有解压成功，所以/config目录没用
ENTRYPOINT ["java","-jar","/portal.jar"]
