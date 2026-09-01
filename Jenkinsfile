pipeline {
    agent any

    // Tools must be configured under: Manage Jenkins > Tools
    // (Names below must match exactly what you name them there.)
    tools {
        maven 'Maven-3.8'
        jdk   'JDK-17'
    }

    // Let the job be started with a chosen browser/suite from the Jenkins UI.
    parameters {
        choice(name: 'BROWSER',  choices: ['chrome', 'firefox', 'edge'], description: 'Browser to run tests on')
        choice(name: 'SUITE',    choices: ['smoke', 'testng'],           description: 'TestNG suite to execute')
        booleanParam(name: 'HEADLESS', defaultValue: true,               description: 'Run headless (required on CI servers)')
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                sh """
                    mvn -B test \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${params.HEADLESS} \
                        -DsuiteXmlFile=src/test/resources/suites/${params.SUITE}.xml
                """
            }
        }
    }

    post {
        always {
            // Publish Surefire (TestNG-backed) XML results so Jenkins shows pass/fail trends.
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
        }
        success { echo 'All tests passed.' }
        failure { echo 'Tests failed — inspect the published report and console log.' }
    }
}
