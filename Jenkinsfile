#!groovy
@Library('werft-commons-jenkins-pipeline')
import net.wissenswerft.commons.jenkins.pipeline.Release

pipeline {
    agent any

    tools {
        jdk 'JDK 15'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '10'))
    }

    stages {
        stage ('Initialize') {
            steps {
                script{
                    docker.withTool('Docker 20.10.5') {
                        withMaven(maven: 'Maven 3.5.2', mavenLocalRepo: '.m2', options: [openTasksPublisher(disabled: true)]) {
                            sh '''\n\
                        echo "PATH = ${PATH}"
                        echo "M2_HOME = ${M2_HOME}"
                    '''
                            sh "git rev-parse --verify ${BRANCH_NAME} && git branch -D ${BRANCH_NAME} || true"
                            sh "git checkout ${BRANCH_NAME}"
                            sh 'mvn clean'
                        }
                    }
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    docker.withTool('Docker 20.10.5') {
                        withMaven(maven: 'Maven 3.5.2', mavenLocalRepo: '.m2') {
                            sh 'mvn compile -U -DskipTests=true -Dci.buildnumber=${BUILD_NUMBER} -Dci.jobname=\"${JOB_NAME}\"'
                            warnings canComputeNew: false, canResolveRelativePaths: false, consoleParsers: [[parserName: 'Java Compiler (javac)'], [parserName: 'Maven']], defaultEncoding: '', excludePattern: '', failedTotalAll: '130', healthy: '0', includePattern: '', messagesPattern: '', unHealthy: '102', unstableTotalAll: '120'
                        }
                    }
                }
            }
        }
        stage('Prepare release') {
            when {
                expression {
                    return currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                withMaven(maven: 'Maven 3.5.2', mavenLocalRepo: '.m2', options: [junitPublisher(disabled: true), openTasksPublisher(disabled: true)]) {
                    sh "mvn -DskipTests=true -Dci.buildnumber=${BUILD_NUMBER} -Dci.jobname=\"${JOB_NAME}\" package"
                }
            }
        }
        stage('Release') {
            when {
                expression {
                    return currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                withMaven(maven: 'Maven 3.5.2', mavenLocalRepo: '.m2', options: [junitPublisher(disabled: true), openTasksPublisher(disabled: true)]) {
                    script {
                        def pom = readMavenPom file: 'pom.xml'
                        def r = new Release(this, "wwjenkinspublishspaceone", "releases.space.one", "/web/modules/one/space/spaceone-module-horus", pom.version, "spaceone-module-horus")
                        r.doRelease('target/spaceone-module-horus*.jar')
                    }
                }
            }
        }
    }
}
