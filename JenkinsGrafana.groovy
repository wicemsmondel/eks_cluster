node {  
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'Cred_AWS']]){
        stage('Grafana') {
            sh '''
            kubectl create namespace grafana
            curl -o grafana-values.yaml https://raw.githubusercontent.com/helm/charts/master/stable/grafana/values.yaml
            helm install grafana stable/grafana --set rbac.create=false --set service.type=LoadBalancer --set persistence.enabled=true --namespace grafana
            kubectl get secret --namespace grafana grafana -o jsonpath="{.data.admin-password}" | base64 --decode ; echo            kubectl --namespace grafana port-forward $POD_NAME 3000
            kubectl get svc --namespace grafana
            '''
        }
    }
}

