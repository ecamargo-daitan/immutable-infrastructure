apiBuildNumberParam = 'API_BUILD_NUMBER'
envNameParam = 'ENVIRONMENT_NAME'
env = params.ENVIRONMENT_NAME

apiBuild = null
bakeBuild = null
pipeline {
    agent any
    options {
        skipDefaultCheckout()
    }
    stages {
        stage('build') {
            steps {
                script {
                    apiBuild = build(
                            job: "build-api",
                            parameters: []
                    )
                }
            }
        }
        stage('deploy') {
            steps {
                script {
                    build(
                            job: "deploy",
                            parameters: [
                                    string(name: apiBuildNumberParam, value: String.valueOf(apiBuild.getNumber())),
                                    string(name: envNameParam, value: env)
                            ]
                    )
                }
            }
        }
    }
}
