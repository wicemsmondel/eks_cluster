node {  
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'Cred_AWS']]){
        stage('Install Grafana') {
            sh '''
            echo '########## INSTALLING GRAFANA ##########'
            kubectl create namespace grafana
            aws s3 cp s3://wicem-grafana/grafana-values.yaml grafana-values.yaml
            helm install grafana stable/grafana --set service.type=LoadBalancer --set persistence.enabled=true --namespace grafana
            '''
        }
        stage('Get Grafana config'){
            sleep 30
            sh'''
            echo '########## CHECKING GRAFANA ##########'
            kubectl get secret --namespace grafana grafana -o jsonpath="{.data.admin-password}" | base64 --decode ; echo
            kubectl get svc --namespace grafana
            '''
        }
    }
}
