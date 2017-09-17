def gitUrl = 'https://github.com/isacssouza/immutable-infrastructure.git'

job('api') {
    scm {
        git(gitUrl)
    }
    steps {
        shell('cd api; ./mvnw -e clean test')
    }
    publishers {
        archiveJunit('**/target/**/TEST*.xml') {
            allowEmptyResults(false)
            retainLongStdout()
            testDataPublishers {
                publishTestStabilityData()
            }
        }
    }
}