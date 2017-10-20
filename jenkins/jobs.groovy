def gitUrl = 'https://github.com/ecamargo/immutable-infrastructure.git'

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
            branch('bt-webinar')
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
        jacocoCodeCoverage {}
        checkstyle {
            canRunOnFailed(true)
        }

    }
}

job('deploy') {
    parameters {
        stringParam(apiBuildNumberParam, null, 'Api build number to get the artifacts from')
        stringParam(envNameParam, null, 'Environment name')
    }
    scm {
        git {
            remote {
                url(gitUrl)
            }
            branch('bt-webinar')
            extensions {
                cleanBeforeCheckout()
            }
        }
    }
    steps {
        copyArtifacts(apiBuildName) {
            buildSelector {
                buildNumber("\$${apiBuildNumberParam}")
            }
            flatten()
            targetDirectory('$WORKSPACE/ansible/playbooks/roles/api/files')
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
            branch('bt-webinar')
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
                    branch('bt-webinar')
                    extensions {
                        cleanBeforeCheckout()
                    }
                }
            }
            scriptPath("jenkins/pipeline.groovy")
        }
    }
    parameters {
        stringParam(envParam, 'test', 'The target environment to configure')
    }
}
