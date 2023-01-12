pipeline{

    agent any
   

  
  options { skipDefaultCheckout() }
  
  
  parameters {
    string(name:'vpc_name', defaultValue: 'vpc_name', description : 'vpc_name')
    string(name:'public_subnets', defaultValue: 'public_subnets', description : 'public_subnets')
    string(name:'azs', defaultValue: 'azs', description : 'Availability Zone')
    string(name:'iam_role_arn', defaultValue: '', description : 'iam_role_arn')
    string(name:'iam_user_name', defaultValue: '', description : 'iam_user_name')
    string(name:'private_subnet_ids', defaultValue: '', description : 'private_subnet_ids')
    string(name:'public_subnet_ids', defaultValue: '', description : 'public_subnet_ids')
	string(name:'region', defaultValue: '', description : 'region')
	string(name:'vpc_cidr', defaultValue: '', description : 'vpc_cidr')
	string(name:'backend_s3_bucket', defaultValue: '', description : 's3-name')
	string(name:'Create_VPC', defaultValue: '', description : 'Create_VPC')
	string(name:'environment_name', defaultValue: '', description : 'environment_name')
	string(name:'private_subnets', defaultValue: '', description : 'private_subnets')
    string(name:'Create_EKS', defaultValue: '', description : 'Create_EKS')
    string(name:'DCP_end_date', defaultValue: '', description : 'DCP_end_date')
    string(name:'DCP_main_contact', defaultValue: '', description : 'DCP_main_contact')
    string(name:'DCP_project_name', defaultValue: '', description : 'DCP_project_name')
    string(name:'DCP_start_date', defaultValue: '', description : 'DCP_start_date')
    string(name:'DCP_WBS', defaultValue: '', description : 'DCP_WBS')
    string(name:'Destroy_All', defaultValue: '', description : 'Destroy_All')
    string(name:'eks_name', defaultValue: '', description : 'eks_name')
	string(name:'Create_s3', defaultValue: '', description : 'Create_s3')
	
  
	
	 } 
	
  



	    stages {

			
       stage('Download all the Artifacts') {
          steps{
			
			
			withCredentials([usernamePassword(credentialsId: 'bitbucket-citi', passwordVariable: 'bitbucket_password', usernameVariable: 'bitbucket_user')]) {
				sh '''
				mkdir -p cloned_repo_${BUILD_NUMBER}
				ls
				cd cloned_repo_${BUILD_NUMBER}
                git config --global credential.username ${bitbucket_user}
                git config --global credential.helper "!echo password=${bitbucket_password}; echo"
				git clone https://bitbucket-dcp.deloitte.dcpgreendot.com/scm/~kgnanesh/dcp-base-tf-modules.git
				git clone https://bitbucket-dcp.deloitte.dcpgreendot.com/scm/~kgnanesh/dcp-core-vpc.git
				git clone https://bitbucket-dcp.deloitte.dcpgreendot.com/scm/~kgnanesh/dcp-infra-app-clusters.git
				git clone https://bitbucket-dcp.deloitte.dcpgreendot.com/scm/~kgnanesh/dcp-pipeline-templates.git
				git clone https://bitbucket-dcp.deloitte.dcpgreendot.com/scm/~kgnanesh/dcp_setup_ingress.git
				git clone https://bitbucket-dcp.deloitte.dcpgreendot.com/scm/~kgnanesh/dcp-setup-scripts.git
			
				pwd
				ls
				
				'''
                }
			  
			}
        }
        
          
          
/*
   stage('Deploy S3') {
        steps {
          script{
          
           withCredentials([[
                $class: 'AmazonWebServicesCredentialsBinding',
                credentialsId: "aws-citi",
                accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                      ]]) {
					  sh '''
                      cd ./cloned_repo_${BUILD_NUMBER}/s3
                      pwd
             		  if [ "${Create_s3}" = "true"]; then
                      echo "jK"
             		  echo bucket = \"${backend_s3_bucket}\" 
             		  echo region = \"${region}\" 
             		  echo DCP_STARTDATE = \"${DCP_start_date}\" 
            		  echo DCP_WBS = \"${WBS}\" 
             		  echo DCP_PROJECTNAME = \"${DCP_project_name}\" 
                      echo DCP_MAINCONTACT = \"${DCP_main_contact}\" 
                      echo DCP_MANAGERCONTACT = \"$DCP_{manager_contact}\" 
                      echo DCP_ENDDATE = \"${DCP_end_date}\" 
             		  echo "AWS S3 Creation Initiated"
            		  sh 'aws --version'
           			  sh 'aws s3 mb s3://"${backend_s3_bucket}" --region "${region}"'
           			  sh 'aws s3api put-bucket-tagging --bucket "${backend_s3_bucket}" --region "${region}" --tagging "TagSet=[{Key=DCP_STARTDATE,Value= ${DCP_start_date}}, {Key=DCP_WBS,Value= ${DCP_WBS}}, {Key=DCP_PROJECTNAME,Value= ${DCP_project_name}}, {Key=DCP_MAINCONTACT,Value= ${DCP_main_contact}}, {Key=DCP_ENDDATE,Value= ${DCP_end_date}} ]"'
            		  sh 'aws s3api get-bucket-tagging --bucket "${backend_s3_bucket}" --region "${region}"'
             		  fi
                      
            		 '''
					 
                  }
          }
        }
	}	
    
*/
            

       

            

       


       stage('Deploy VPC'){
        
            steps{
              withCredentials([[
                $class: 'AmazonWebServicesCredentialsBinding',
                credentialsId: "aws-citi",
                accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
              ]]){
                      sh '''
                       pwd                 
                        ls
                        echo $vpc_name
                        echo $public_subnet
                        echo $vpc_name
                        echo $public_subnet
                        echo $Create_VPC
                        echo huhu
                        public_subnets= jq -cR '. | gsub("^ +| +$"; "") | split(" *, *"; "")' <<< ${public_subnets}
                        echo haha
                        echo ${public_subnets}
                        private_subnets=jq -cR '. | gsub("^ +| +$"; "") | split(" *, *"; "")' <<< ${private_subnets}
                        echo ${private_subnets}
                        

                        if [ "${Create_VPC}" = true ]; then
                        cd ./cloned_repo_${BUILD_NUMBER}/dcp-core-vpc/vpc
                        pwd
                        ls
                        tmp_vars_dir="tmp-vars"
                        mkdir -p ${tmp_vars_dir}

                        vpc_name="dcp-${environment_name}-core-vpc"
                        azs=[${azs}]
                        echo ${private_subnets}
                        echo ${public_subnets}
                        echo ${azs}
                        echo ${Create_EKS}
                        echo ${Create_VPC}

                        echo "BUILD NUMBER is : "${BUILD_NUMBER}
                        echo region = \\"${region}\\" > ${tmp_vars_dir}/vpc.tfvars
                        echo name = \"${vpc_name}\" >> ${tmp_vars_dir}/vpc.tfvars
                        echo cidr = \\"${vpc_cidr}\\" >> ${tmp_vars_dir}/vpc.tfvars
                        echo azs = ${azs} >> ${tmp_vars_dir}/vpc.tfvars
                        echo backend_s3_bucket = ${backend_s3_bucket} >> ${tmp_vars_dir}/vpc.tfvars
                        echo public_subnets = ${public_subnets} >> ${tmp_vars_dir}/vpc.tfvars
                        echo private_subnets = ${private_subnets} >> ${tmp_vars_dir}/vpc.tfvars
                        
                        echo DCP_STARTDATE = \\"${DCP_start_date}\\" >> ${tmp_vars_dir}/vpc.tfvars
                        echo DCP_WBS = \\"${DCP_WBS}\\" >> ${tmp_vars_dir}/vpc.tfvars
                        echo DCP_PROJECTNAME = \\"${DCP_project_name}\\" >> ${tmp_vars_dir}/vpc.tfvars
                        echo DCP_MAINCONTACT = \\"${DCP_main_contact}\\" >> ${tmp_vars_dir}/vpc.tfvars
                        echo DCP_MANAGERCONTACT = \\"${DCP_manager_contact}\\" >> ${tmp_vars_dir}/vpc.tfvars
                        echo DCP_ENDDATE = \\"${DCP_end_date}\\" >> ${tmp_vars_dir}/vpc.tfvars
                        cat ${tmp_vars_dir}/vpc.tfvars
                        
                        cat ${tmp_vars_dir}/vpc.tfvars
						echo "\nDeleting old TF state files\n"
                        echo ${backend_s3_bucket}

						rm -rf .terraform terraform.tfstate terraform.tfstate.backup

						terraform init -backend-config="region=${region}" -backend-config="bucket=${backend_s3_bucket}" -backend-config="key=${region}/dcp-core-services/eks" -backend-config="encrypt=true" -no-color
                        terraform plan -var-file=${tmp_vars_dir}/vpc.tfvars -var="name=${vpc_name}" -no-color
                        
                        fi
                        


                       
                     
                        '''
              }
                 }          
         
  } 

            stage('Terraform check'){
        
            steps{
              
                
                
                        sh '''
                       

                        terraform version
                     
                        '''
                    
                 
             
         }



        } 

 
         
         
         stage('test'){
           
           steps{
             
           sh '''
           echo "testing"
           '''
           
           }
         
         
         }
	  
	     

                
               
                


    }

}


