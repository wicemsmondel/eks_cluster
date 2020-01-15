node {  
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'Cred_AWS']]){
        stage('Add cluster admin') {
            sh '''
            echo '##########ADD CLUSTER ADMIN##########'
            aws eks --region ${REGION} update-kubeconfig --name ${CLUSTERNAME}
            eksctl create iamidentitymapping --cluster ${CLUSTERNAME} --region ${REGION} --arn ${USERARN} --group ${USERGROUP} --username ${USERNAME}
            '''
        }
    }
}
