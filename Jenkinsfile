pipeline {
    agent any

    stages {
        stage('Building') {
            steps {
                echo "Building the dev project..."
            }
        }

        stage('Testing') {
            steps {
                echo "Building the test project..."
                git branch: 'main', credentialsId: '3b6f0b2c-13a5-4ee4-af4b-81d93cfe3f89', url: 'https://github.com/khnanayakkara/PageObjFactorySeleniumJava.git'
                sh 'mvn clean compile'
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploy code to staging server..."
            }
        }
    }

}
