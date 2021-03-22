pipeline {
    agent { label 'docker' }
    stages {
        stage('Build') {
            steps {
                withDockerRegistry([credentialsId: 'fintlabsacr.azurecr.io', url: 'https://fintlabsacr.azurecr.io']) {
                    sh "docker pull fintlabsacr.azurecr.io/kunde-selvregistrering-frontend:latest"
                    sh "docker build --tag ${GIT_COMMIT} ."
                }
            }
        }
        stage('Publish') {
            when { branch 'master' }
            steps {
                script {
                    VERSION = GIT_COMMIT[0..6]
                }
                withDockerRegistry([credentialsId: 'fintlabsacr.azurecr.io', url: 'https://fintlabsacr.azurecr.io']) {
                    sh "docker tag ${GIT_COMMIT} fintlabsacr.azurecr.io/kunde-selvregistrering:${VERSION}"
                    sh "docker push fintlabsacr.azurecr.io/kunde-selvregistrering:${VERSION}"
                }
                kubernetesDeploy configs: 'k8s.yaml', kubeconfigId: 'aks-beta-fint'
            }
        }
        stage('Publish PR') {
            when { changeRequest() }
            steps {
                script {
                    VERSION = BRANCH_NAME + '.' + GIT_COMMIT[0..6]
                }
                withDockerRegistry([credentialsId: 'fintlabsacr.azurecr.io', url: 'https://fintlabsacr.azurecr.io']) {
                    sh "docker tag ${GIT_COMMIT} fintlabsacr.azurecr.io/kunde-selvregistrering:${VERSION}"
                    sh "docker push fintlabsacr.azurecr.io/kunde-selvregistrering:${VERSION}"
                }
                kubernetesDeploy configs: 'k8s.yaml', kubeconfigId: 'aks-beta-fint'
            }
        }
        stage('Publish Version') {
            when {
                tag pattern: "v\\d+\\.\\d+\\.\\d+(-\\w+-\\d+)?", comparator: "REGEXP"
            }
            steps {
                script {
                    VERSION = TAG_NAME[1..-1]
                }
                sh "docker tag ${GIT_COMMIT} fintlabsacr.azurecr.io/kunde-selvregistrering:${VERSION}"
                withDockerRegistry([credentialsId: 'fintlabsacr.azurecr.io', url: 'https://fintlabsacr.azurecr.io']) {
                    sh "docker push fintlabsacr.azurecr.io/kunde-selvregistrering:${VERSION}"
                }
                kubernetesDeploy configs: 'k8s.yaml', kubeconfigId: 'aks-beta-fint'
            }
        }
    }
}

