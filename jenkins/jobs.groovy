def gitUrl = 'https://github.com/isacssouza/immutable-infrastructure.git'

apiBuildNumberParam = 'API_BUILD_NUMBER'
bakeBuildNumberParam = 'BAKE_BUILD_NUMBER'
envNameParam = 'ENVIRONMENT_NAME'

apiBuildName = 'build-api'
job(apiBuildName) {
    scm {
        git {
            remote {
                url(gitUrl)
            }
            extensions {
                cleanBeforeCheckout()
            }
        }
    }
    steps {
        shell('cd api; ./mvnw -e --batch-mode -T2C clean install')
    }
    publishers {
        archiveArtifacts('**/target/*.jar')
        archiveJunit('**/target/**/TEST*.xml') {
            allowEmptyResults(false)
            retainLongStdout()
            testDataPublishers {
                publishTestStabilityData()
            }
        }
    }
}

job('deploy') {
    parameters {
        stringParam(apiBuildNumberParam, null, 'Api build number to get the artifacts from')
    }
    scm {
        git {
            remote {
                url(gitUrl)
            }
            extensions {
                cleanBeforeCheckout()
            }
        }
    }
    steps {
        copyArtifacts(bakeBuildName) {
            buildSelector {
                buildNumber("\$${apiBuildNumberParam}")
            }
            flatten()
            targetDirectory('$WORKSPACE/deploy/')
            optional(false)
        }
        shell("jenkins/deploy.sh")
    }
}

job('createEnvironment') {
    parameters {
        stringParam(envNameParam, null, 'Environment name')
    }
    scm {
        git {
            remote {
                url(gitUrl)
            }
            extensions {
                cleanBeforeCheckout()
            }
        }
    }
    steps {
        shell("jenkins/createEnvironment.sh")
    }
}


pipelineJob('pipeline') {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(gitUrl)
                    }
                    extensions {
                        cleanBeforeCheckout()
                    }
                }
            }
            scriptPath("jenkins/pipeline.groovy")
        }
    }
}
