# mock
本项目主要用于mock dubbo
1、需要mock的项目pom.xml文件中加入
<profiles>
		<profile>
			<id>dubbo-mock</id>
			<dependencies>
				<dependency>
					<groupId>com.pinganfu.mockall</groupId>
					<artifactId>dubbo-mock</artifactId>
					<version>1.0.0-SNAPSHOT</version>
				</dependency>
			</dependencies>
		</profile>
	</profiles>
	
	2、maven构建时指定profile
	mvn -P dubbo-mock clean install
	
	3、应用启动时指定dubboMockServer地址
	export JAVA_OPTS="$JAVA_OPTS -DdubboMockServer=http://";./start.sh
