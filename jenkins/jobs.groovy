def gitUrl = 'https://github.com/isacssouza/immutable-infrastructure.git'

apiBuildNumberParam = 'API_BUILD_NUMBER'
bakeBuildNumberParam = 'BAKE_BUILD_NUMBER'

apiBuildName = 'build-api'
job(apiBuildName) {
    scm {
        git {
            remote {
                url(gitUrl)
            }
            branch('master')
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

bakeBuildName = 'bake'
job(bakeBuildName) {
    parameters {
        stringParam(apiBuildNumberParam, null, 'Api build number to get the artifacts from')
    }
    scm {
        git {
            remote {
                url(gitUrl)
            }
            branch('master')
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
            targetDirectory('$WORKSPACE/packer/')
            optional(false)
        }
        shell("jenkins/bake.sh")
    }
    publishers {
        archiveArtifacts {
            pattern('packer/ami-id.txt')
            onlyIfSuccessful()
        }
    }
}

job('deploy') {
    parameters {
        stringParam(bakeBuildNumberParam, null, 'Bake build number to get the artifacts from')
    }
    scm {
        git {
            remote {
                url(gitUrl)
            }
            branch('master')
            extensions {
                cleanBeforeCheckout()
            }
        }
    }
    steps {
        copyArtifacts(bakeBuildName) {
            buildSelector {
                buildNumber("\$${bakeBuildNumberParam}")
            }
            flatten()
            targetDirectory('$WORKSPACE/deploy/')
            optional(false)
        }
        shell("jenkins/deploy.sh")
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
                    branch('master')
                    extensions {
                        cleanBeforeCheckout()
                    }
                }
            }
            scriptPath("jenkins/pipeline.groovy")
        }
    }
}
