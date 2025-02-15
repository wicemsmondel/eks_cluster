# EKS Cluster
## Automatisation du déploiement d'un cluster EKS paramétrable avec Jenkins

### 1.Instanciation d'une EC2
1. Sélection d'une AMI Ubuntu Server 18.04
2. Sélection d'une instance de type t3.small
3. Configuration de EC2 (VPC, subnet, public IP...) et utilisation d'un fichier user data
4. Ajout d'un stockage gp2 de 8GiB
5. Ajout de tags
6. Configuration du Security Group pour établir une communication ssh sur le port 22 et http sur le custom port 8080

Le fichier 'User data' utilisé pour que toutes les dépendances nécessaires soient installées au démarrage de l'EC2 :
```
#!/bin/bash
sudo -i
apt update
apt install -y default-jre
wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
apt-get update
apt-get install -y jenkins
apt install -y python3-pip
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin:~/.local/bin/
pip3 install  awscli --upgrade --user
curl --silent --location "https://github.com/weaveworks/eksctl/releases/download/latest_release/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
eksctl version
curl -o kubectl https://amazon-eks.s3-us-west-2.amazonaws.com/1.14.6/2019-08-22/bin/linux/amd64/kubectl
chmod +x ./kubectl
mkdir -p $HOME/bin && cp ./kubectl $HOME/bin/kubectl && export PATH=$HOME/bin:$PATH
echo 'export PATH=$HOME/bin:$PATH' >> ~/.bashrc
curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3
chmod 700 get_helm.sh
./get_helm.sh
```
### Connexion à l'EC2
1. Connexion ssh
```
ssh -i "<clé.pem>" ubuntu@ec2-<public ip de l'EC2>.<region>.compute.amazonaws.com
```
2. Connexion à Jenkins 

http://EC2publicIP:8080/login

unlock
```
cat /var/lib/jenkins/secrets/initialAdminPassword
```
install plugin CloudBees AWS Credentials
credentials
jobs

curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3
chmod 700 get_helm.sh
./get_helm.sh