node {
    def pom
    def commitId

    try{
        stage('Clone repository') {
            echo '----------Stage 1 out of 7----------'
            echo 'Checking if the repository is cloned properly..'
            checkout scm
        }

        stage('Initializing and reading variables') {
            echo '----------Stage 2 out of 7----------'
            echo 'Reading pom file..'
            pom = readMavenPom file: 'pom.xml'

            echo 'Reading commit ID..'
            commitId = sh(returnStdout: true, script: 'git rev-parse HEAD | xargs')
            commitId = commitId.trim()

            echo "groupId: ${pom.groupId} \n artifactId: ${pom.artifactId}\n packaging: ${pom.packaging}\n version: ${pom.version}\n name: ${pom.name}\n Commit ID: ${commitId}"

            echo 'Notifying bitbucket build has started..'
            this.notifyBitbucket('INPROGRESS', commitId)
            echo 'Bitbucket notified!'
        }

        stage('Test') {
            echo '----------Stage 3 out of 7----------'
            echo 'Testing..'
            try {
                sh 'mvn test'
                junit 'target/surefire-reports/*.xml'
            } catch (err) {
                slackSend color: 'danger', message: "Tests failed for branch: ${env.JOB_NAME} build number: ${env.BUILD_NUMBER} URL: ${env.BUILD_URL}"
                throw err;
            }
            slackSend color: 'good', message: "Tests succeeded for branch: ${env.JOB_NAME} build number: ${env.BUILD_NUMBER} URL: ${env.BUILD_URL}"
        }

        stage('Build') {
            echo '----------Stage 4 out of 7----------'
            echo 'Building..'
            try {
                sh 'mvn jfx:jar -Dmaven.test.skip=true'
            } catch (err) {
                slackSend color: 'danger', message: "Build failed for branch: ${env.JOB_NAME} build number: ${env.BUILD_NUMBER} URL: ${env.BUILD_URL}"
                throw err;
            }
            slackSend color: 'good', message: "Build succeeded for branch: ${env.JOB_NAME} build number: ${env.BUILD_NUMBER} URL: ${env.BUILD_URL}"
        }

        stage('SonarQube analysis') {
            echo '----------Stage 5 out of 7----------'
            echo 'SonarQube feature analysis..'

            def projectKey = env.JOB_NAME.replace("/", "-").replace("%", "")
            sh "mvn sonar:sonar -Dsonar.host.url=http://localhost:7001 -Dsonar.login=849182d1399c7fc7e3b2a68bdc6b58b1c1c4ec8e -Dsonar.projectKey=${projectKey} -Dsonar.projectName=${env.JOB_NAME}"
            def response = httpRequest "http://localhost:7001/api/qualitygates/project_status?projectKey=${projectKey}"


try {
            def json = readJSON text: response.content;
            if (json.projectStatus.status == "ERROR") {
                slackSend color: 'danger', message: "SonarQube failed for branch: ${env.JOB_NAME} build number: ${env.BUILD_NUMBER} URL: ${env.BUILD_URL}"

            }
                    } catch(err) {

                    }
        }

        stage('Copying') {
            echo '----------Stage 6 out of 7----------'
            if(env.BRANCH_NAME.equals('master')||env.BRANCH_NAME.equals('development')) {
                echo 'Copying to download folder..'
                sh "cp target/jfx/app/${pom.artifactId}-${pom.version}-jfx.jar /deployment/downloads/${env.BRANCH_NAME}/${pom.artifactId}-${pom.version}-jfx.jar"
            } else {
                echo 'Stage 6 (Copying) skipped since this branch is not master or development'
            }
        }



        stage('Notify Bitbucket') {
            echo '----------Stage 7 out of 7----------'
            echo 'Notifying Bitbucket of the successful build..'
            this.notifyBitbucket('SUCCESS', commitId)
        }


    } catch(err) {
        echo '!!!!!!!!!!!!!!!!!!!!!!!!!BUILD FAILURE!!!!!!!!!!!!!!!!!!!!!!!!!'
        echo 'Error message:'
        echo err.toString()
        echo 'Notifying Bitbucket of the failed build..'
        this.notifyBitbucket('FAILED', commitId)
    }
}


def notifyBitbucket(String state, String commitId) {

    if('SUCCESS' == state || 'FAILED' == state) {
        currentBuild.result = state         // Set result of currentBuild !Important!
    }

    notifyBitbucket commitSha1: commitId,
                credentialsId: '1953f581-d6b9-471e-8727-a52e05be0acb',
                disableInprogressNotification: false,
                considerUnstableAsSuccess: false,
                ignoreUnverifiedSSLPeer: true,
                includeBuildNumberInKey: false,
                prependParentProjectKey: false,
                projectKey: 'beergame',
                stashServerBaseUrl: 'http://10.20.2.10:7990/stash'
}