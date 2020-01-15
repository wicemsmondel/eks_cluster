node {  
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'Cred_AWS']]){
        stage ('Create namespace Prometheus') {
            sh'''
            aws eks --region ${REGION} update-kubeconfig --name ${CLUSTERNAME}
            kubectl create namespace prometheus
            '''
        }
        stage ('Set Helm repo') {
            sh'''
            helm repo add stable https://kubernetes-charts.storage.googleapis.com/
            helm repo update
            '''
        }
        stage ('Install Prometheus') {
            sh'''
            helm install prometheus stable/prometheus  --namespace prometheus
            '''
        }
    }
}

