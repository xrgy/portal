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
#COPY  . /myportal

#RUN apt-get -y install wget git unzip
#RUN unzip portal-1.0.jar -d /portal

EXPOSE 8082
#CMD /usr/local/apache-tomcat-8.5.34/bin/catalina.sh run
#CMD ["--spring.config.location=/config/application.properties"] jar包没有解压成功，所以/config目录没用

EXPOSE 30003

#  "-Djava.rmi.server.hostname=47.94.157.199",\
#            "-Dcom.sun.management.jmxremote=true",\
#            "-Dcom.sun.management.jmxremote.port=30003",\
#            "-Dcom.sun.management.jmxremote.rmi.port=30003",\
#            "-Dcom.sun.management.jmxremote.ssl=false",\
#            "-Dcom.sun.management.jmxremote.authenticate=false",\
#            "-Dcom.sun.management.jmxremote.local.only=false",\

ENTRYPOINT ["java",\
            "-Djava.rmi.server.hostname=47.94.157.199",\
            "-Dcom.sun.management.jmxremote=true",\
            "-Dcom.sun.management.jmxremote.port=30003",\
            "-Dcom.sun.management.jmxremote.rmi.port=30003",\
            "-Dcom.sun.management.jmxremote.ssl=false",\
            "-Dcom.sun.management.jmxremote.authenticate=false",\
            "-Dcom.sun.management.jmxremote.local.only=false",\
            "-jar","/portal.jar"]
