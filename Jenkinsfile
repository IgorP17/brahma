pipeline {
    agent any

    parameters {
        booleanParam(name: 'DEPLOY_WEBUI',          defaultValue: false, description: 'Собрать и задеплоить WebUI')
        booleanParam(name: 'DEPLOY_GATEWAY',        defaultValue: false, description: 'Собрать и задеплоить Gateway')
        booleanParam(name: 'DEPLOY_PROCESSOR',      defaultValue: false, description: 'Собрать и задеплоить Processor')
        booleanParam(name: 'CLEAN_MAVEN_CACHE',     defaultValue: false, description: '⚡ Фигачить кэш Maven (.m2/repository) перед сборкой')
    }

    environment {
        K8S_NAMESPACE = 'default'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean Maven Cache') {
            when { expression { params.CLEAN_MAVEN_CACHE } }
            steps {
                echo "🧹 Фигачим кэш Maven..."
                sh 'rm -rf ~/.m2/repository/*'
            }
        }

        stage('Maven Build & Tests') {
            when {
                expression { params.DEPLOY_WEBUI || params.DEPLOY_GATEWAY || params.DEPLOY_PROCESSOR }
            }
            steps {
                sh 'mvn clean install -Dquarkus.package.jar.type=uber-jar'
            }
        }

        stage('Deploy WebUI') {
            when { expression { params.DEPLOY_WEBUI } }
            steps {
                script {
                    def app = 'brahma-webui'
                    def image = "${app}:${IMAGE_TAG}"
                    echo "🚀 Building and deploying ${app}..."
                    dir('brahma-webui') {
                        sh "eval \$(minikube docker-env) && docker build -t ${image} ."
                    }
                    sh """
                        kubectl apply -f k8s/brahma-webui.yaml -n ${K8S_NAMESPACE}
                        kubectl set image deployment/${app} ${app}=${image} -n ${K8S_NAMESPACE}
                        kubectl rollout status deployment/${app} -n ${K8S_NAMESPACE} --timeout=120s
                    """
                    def minikubeIp = sh(script: 'minikube ip', returnStdout: true).trim()
                    sh """
                        for i in 1 2 3 4 5; do
                            echo "Attempt \$i/5..."
                            if curl -sf http://${minikubeIp}:30882 > /dev/null 2>&1; then
                                echo "WebUI is responding!"
                                exit 0
                            fi
                            sleep 15
                        done
                        echo "WebUI did not respond after 5 attempts"
                        exit 1
                    """
                }
            }
        }

        stage('Deploy Gateway') {
            when { expression { params.DEPLOY_GATEWAY } }
            steps {
                script {
                    def app = 'brahma-gateway'
                    def image = "${app}:${IMAGE_TAG}"
                    echo "🚀 Building and deploying ${app}..."
                    dir('brahma-gateway') {
                        sh "eval \$(minikube docker-env) && docker build -t ${image} ."
                    }
                    sh """
                        kubectl apply -f k8s/brahma-gateway.yaml -n ${K8S_NAMESPACE}
                        kubectl set image deployment/${app} ${app}=${image} -n ${K8S_NAMESPACE}
                        kubectl rollout status deployment/${app} -n ${K8S_NAMESPACE} --timeout=120s
                    """
                    def minikubeIp = sh(script: 'minikube ip', returnStdout: true).trim()
                    sh """
                        for i in 1 2 3 4 5; do
                            echo "Attempt \$i/5..."
                            if curl -sf http://${minikubeIp}:30880/q/health/live > /dev/null 2>&1; then
                                echo "Gateway is responding!"
                                exit 0
                            fi
                            sleep 15
                        done
                        echo "Gateway did not respond after 5 attempts"
                        exit 1
                    """
                }
            }
        }

        stage('Deploy Processor') {
            when { expression { params.DEPLOY_PROCESSOR } }
            steps {
                script {
                    def app = 'brahma-processor'
                    def image = "${app}:${IMAGE_TAG}"
                    echo "🚀 Building and deploying ${app}..."
                    dir('brahma-processor') {
                        sh "eval \$(minikube docker-env) && docker build -t ${image} ."
                    }
                    sh """
                        kubectl apply -f k8s/brahma-processor.yaml -n ${K8S_NAMESPACE}
                        kubectl set image deployment/${app} ${app}=${image} -n ${K8S_NAMESPACE}
                        kubectl rollout status deployment/${app} -n ${K8S_NAMESPACE} --timeout=120s
                    """
                    echo "Waiting for ${app} pod to be ready..."
                    sh """
                        for i in 1 2 3 4 5; do
                            echo "Attempt \$i/5..."
                            if kubectl get pods -n ${K8S_NAMESPACE} -l app=${app} --field-selector=status.phase=Running | grep -q '1/1'; then
                                echo "${app} is Running and Ready!"
                                exit 0
                            fi
                            sleep 15
                        done
                        echo "${app} did not become ready after 5 attempts"
                        exit 1
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                // 1. Чистим рабочую папку Jenkins (исходники, target/, .jar)
                echo "🧹 Cleaning Jenkins workspace..."
                cleanWs()

                // 2. Чистим старые Docker-образы в Minikube (теперь без фильтра по времени)
                echo "🧹 Cleaning up unused Docker images in Minikube..."
                sh 'eval $(minikube docker-env) && docker image prune -a -f'

                // 3. Чистим кэш сборщика Docker
                sh 'eval $(minikube docker-env) && docker builder prune -f'
            }
        }
        success {
            script {
                def deployed = []
                if (params.DEPLOY_WEBUI) deployed << 'WebUI'
                if (params.DEPLOY_GATEWAY) deployed << 'Gateway'
                if (params.DEPLOY_PROCESSOR) deployed << 'Processor'

                if (deployed.size() > 0) {
                    echo "✅ Успешно задеплоены: ${deployed.join(', ')} (версия ${IMAGE_TAG})"
                } else {
                    echo "⚠️ Сборка завершена, но ни один сервис не был выбран для деплоя."
                }
            }
        }
        failure {
            echo "❌ Деплой провален. Проверь логи."
        }
    }
}