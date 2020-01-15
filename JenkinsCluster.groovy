node {  
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'Cred_AWS']]){
        stage('Create yaml') {
            sh '''
            echo '########## CREATING YAML ##########'
            echo 'apiVersion: eksctl.io/v1alpha5' >> cluster${BUILD_NUMBER}.yaml
            echo 'kind: ClusterConfig' >> cluster${BUILD_NUMBER}.yaml
            echo 'metadata:' >> cluster${BUILD_NUMBER}.yaml
            echo '  name: '${CLUSTERNAME}${BUILD_NUMBER} >> cluster${BUILD_NUMBER}.yaml
            echo '  region: '${REGION} >> cluster${BUILD_NUMBER}.yaml
            '''
            
            if (VPC == "default"){
                sh'''
                echo '########## SETTING DEFAULT VPC ##########'
                '''
                }else{
                   sh '''
                   echo '########## SETTING VPC CONFIGURATION ##########'
                   echo 'vpc:' >> ./cluster.yaml
                   echo "  id: ${VPC}" >> ./cluster.yaml
                   echo "  cidr: ${VPCCIDR}" >> ./cluster.yaml"
                   echo "  subnets:" >> ./cluster.yaml
                   echo "    private:" >> ./cluster.yaml
                   echo "      ${REGION}:" >> ./cluster.yaml
                   echo "        ${SUBNET}:" >> ./cluster.yaml
                   echo "        ${SUBNETCIDR}:" >> ./cluster.yaml
                  '''
                }
                sh '''
                echo '########## SETTING WORKERNODES ##########'
                echo 'nodeGroups:' >> cluster${BUILD_NUMBER}.yaml
                echo '  - name: '${WORKERNODESNAME} >> cluster${BUILD_NUMBER}.yaml
                echo '    instanceType: '${INSTANCETYPE} >> cluster${BUILD_NUMBER}.yaml
                echo '    desiredCapacity: '${DESIREDCAPACITY} >> cluster${BUILD_NUMBER}.yaml
                '''
        }
        stage('Archive and validate config'){
            sh'''
            echo '########## WAITING FOR VALIDATION ##########'
            '''
            input 'Do you validate this configuration?'
            archiveArtifacts '*.yaml'
        }
        stage('Execute yaml') {
            sh'''
            echo '########## EXECUTING YAML ##########'
            cat cluster${BUILD_NUMBER}.yaml
            eksctl create cluster -f cluster${BUILD_NUMBER}.yaml
            '''
        }
        stage('Add cluster admin') {
            sh '''
            echo '########## ADDING CLUSTER ADMIN ##########'
            aws eks --region ${REGION} update-kubeconfig --name ${CLUSTERNAME}${BUILD_NUMBER}
            eksctl create iamidentitymapping --cluster ${CLUSTERNAME}${BUILD_NUMBER} --region ${REGION} --arn ${USERARN} --group ${USERGROUP} --username ${USERNAME}
            '''
        }
        stage ('Create namespace Prometheus') {
            sh'''
            echo '########## CREATING PROMETHEUS NAMESPACE ##########'
            kubectl create namespace prometheus
            '''
        }
        stage ('Set Helm repo') {
            sh'''
            echo '########## SETTING HELM REPO ##########'
            helm repo add stable https://kubernetes-charts.storage.googleapis.com/
            helm repo update
            '''
        }
        stage ('Install Prometheus') {
            sh'''
            echo '########## INSTALLING HELM ##########'
            helm install prometheus stable/prometheus  --namespace prometheus
            '''
        }
        stage ('Create namespace Grafana') {
            sh'''
            echo '########## CREATING GRAFANA NAMESPACE ##########'
            kubectl create namespace grafana
            '''
        }
    }
}