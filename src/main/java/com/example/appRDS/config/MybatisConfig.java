package com.example.appRDS.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class MybatisConfig {

    public MybatisConfig(@Lazy DataSource dataSource) { // 🔥 Lazy 로딩 추가
        this.dataSource = dataSource;
    }


    @Value(value = "${ssh.host}")
    String sshHost;

    @Value(value = "${ssh.user}")
    String sshUsername;

    @Value(value = "${ssh.ssh_port}")
    int sshPort;

    @Value(value = "${ssh.private_key}")
    String sshPrivateKey;

    @Value(value = "${ssh.passphrase}")
    String sshPassphrase;

    @Value(value = "${ssh.local_port}")
    int sshLocalPort;

    @Value(value = "${ssh.remote_host}")
    String sshRemoteHost;

    @Value(value = "${ssh.remote_port}")
    int sshRemotePort;

    @Value(value = "${spring.datasource.driver-class-name}")
    String driverClassName;

    @Value(value = "${spring.datasource.url}")
    String jdbcUrl;

    @Value(value = "${spring.datasource.username}")
    String dbUserName;

    @Value(value = "${spring.datasource.password}")
    String dbUserPw;

    private final DataSource dataSource;

    @Bean
    public DataSource dataSource() throws JSchException {
        // ssh 연결 위해 JSch 생성
        JSch jsch = new JSch();

        // priavte_key 경로 (pem, ppk, openSSH) / 해당 경로 pem, ppk, openSSH 암호가 걸려있는 경우 같이 넣어준다
        jsch.addIdentity(sshPrivateKey, sshPassphrase);

        // ssh 정보들 넣어주기 
        Session session = jsch.getSession(sshUsername, sshHost, sshPort);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        // ssh 정보들 넣어주기
        int localPort = session.setPortForwardingL(sshLocalPort, sshRemoteHost, sshRemotePort);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        // jdbc 정보 넣어주기
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(dbUserName);
        dataSource.setPassword(dbUserPw);

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        return new DataSourceTransactionManager(dataSource());
    }
}