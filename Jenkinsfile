pipeline {
    agent { label 'docker' }
    stages {
        stage('Build') {
            steps {
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh "docker pull dtr.fintlabs.no/beta/kunde-selvregistrering-frontend:latest"
                    sh "docker build --tag ${GIT_COMMIT} ."
                }
            }
        }
        stage('Publish') {
            when { branch 'master' }
            steps {
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh "docker tag ${GIT_COMMIT} dtr.fintlabs.no/beta/kunde-selvregistrering-backend:latest"
                    sh "docker push 'dtr.fintlabs.no/beta/kunde-selvregistrering-backend:latest'"
                }
                withDockerServer([credentialsId: "ucp-fintlabs-jenkins-bundle", uri: "tcp://ucp.fintlabs.no:443"]) {
                    //sh "docker service update kunde-selvregistrering-backend-beta_kunde-selvregistrering-backend --image dtr.fintlabs.no/beta/kunde-selvregistrering-backend:latest --detach=false"
                }
            }
        }
        stage('Publish PR') {
            when { changeRequest() }
            steps {
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh "docker tag ${GIT_COMMIT} dtr.fintlabs.no/beta/kunde-selvregistrering-backend:${BRANCH_NAME}"
                    sh "docker push 'dtr.fintlabs.no/beta/kunde-selvregistrering-backend:${BRANCH_NAME}'"
                }
            }
        }
        stage('Publish Tag') {
            when { buildingTag() }
            steps {
                withDockerRegistry([credentialsId: 'dtr-fintlabs-no', url: 'https://dtr.fintlabs.no']) {
                    sh "docker tag ${GIT_COMMIT} dtr.fintlabs.no/beta/kunde-selvregistrering-backend:${TAG_NAME}"
                    sh "docker push 'dtr.fintlabs.no/beta/kunde-selvregistrering-backend:${TAG_NAME}'"
                }
            }
        }
    }
}