# External DNS Installation and Automate Registration with R53

Yes, you can use **ExternalDNS** to automate the process of managing DNS records in AWS Route 53, eliminating the need to manually create and maintain the Route 53 entries that map your domain to the AWS Elastic Load Balancer (ELB). Here’s how you can achieve the same setup with **ExternalDNS**.

### Steps to Set Up ExternalDNS with AWS Route 53

1. **Install ExternalDNS in Your Cluster**:
   ExternalDNS needs to be installed in your Kubernetes cluster to manage DNS records. You can deploy it using a Helm chart or a YAML manifest.

   Example Helm command:

    ```bash
    helm repo add bitnami <https://charts.bitnami.com/bitnami>
    helm install external-dns bitnami/external-dns \\
      --set provider=aws \\
      --set aws.zoneType=public \\
      --set txtOwnerId=my-cluster
    
    ```

2. **Configure IAM Permissions for ExternalDNS**:
   ExternalDNS requires permissions to modify Route 53 DNS records. You need to create an IAM role or user that ExternalDNS can use to interact with Route 53.

   Create a policy like the one below:

    ```json
    {
      "Version": "2012-10-17",
      "Statement": [
        {
          "Effect": "Allow",
          "Action": [
            "route53:ChangeResourceRecordSets",
            "route53:ListHostedZones",
            "route53:ListResourceRecordSets"
          ],
          "Resource": ["arn:aws:route53:::hostedzone/YOUR-HOSTED-ZONE-ID"]
        }
      ]
    }
    
    ```

   Attach this policy to an IAM role or user that ExternalDNS can use.

3. **Set Up Ingress and Annotations**:
   You will define a Kubernetes **Ingress Resource** with specific annotations that tell ExternalDNS to create DNS records in Route 53 for the domain.
    - The `external-dns.alpha.kubernetes.io/hostname` annotation is used to specify the DNS name to create.
    - If you want to route traffic to the ELB, ExternalDNS can manage that via the `external-dns.alpha.kubernetes.io/target` annotation.

   Here’s an example of an ingress that uses ExternalDNS to automatically create DNS records:

    ```yaml
    apiVersion: networking.k8s.io/v1
    kind: Ingress
    metadata:
      name: my-app-ingress
      annotations:
        external-dns.alpha.kubernetes.io/hostname: my-app.example.com
        external-dns.alpha.kubernetes.io/target: my-elb-12345.region.elb.amazonaws.com
        nginx.ingress.kubernetes.io/rewrite-target: /
    spec:
      rules:
      - host: "my-app.example.com"
        http:
          paths:
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: my-app-service
                port:
                  number: 80
    
    ```

4. **How ExternalDNS Works in This Setup**:
    - ExternalDNS watches for changes in Kubernetes ingress resources (or services, depending on your configuration).
    - When you create an ingress with the `external-dns.alpha.kubernetes.io/hostname` annotation, ExternalDNS automatically creates or updates the DNS record in Route 53.
    - In this example, it will create an `A` or `CNAME` record in your Route 53 hosted zone for `my-app.example.com` that points to the specified ELB (`my-elb-12345.region.elb.amazonaws.com`).

   Now, your DNS record is dynamically managed. When the ELB IP changes or if you need to point the domain to a different target, you can update your ingress, and ExternalDNS will automatically handle the changes in Route 53.

5. **Route 53 DNS Record**:
   With the above setup, ExternalDNS will ensure that there is a Route 53 DNS record for `my-app.example.com` pointing to the ELB (`my-elb-12345.region.elb.amazonaws.com`). This is the same behavior that you described earlier but automated through ExternalDNS.
6. **Traffic Flow** (Similar to your original setup):
    - Client requests `my-app.example.com`.
    - Route 53 resolves the domain to the ELB DNS (`my-elb-12345.region.elb.amazonaws.com`).
    - ELB routes traffic to the NGINX ingress controller.
    - NGINX ingress controller routes the traffic to the correct service and pod inside the cluster based on the ingress rules.

### Benefits of Using ExternalDNS:

- **Automation**: No need to manually create or update DNS records in Route 53. Any change in your ingress or services automatically reflects in Route 53.
- **Consistency**: Helps avoid errors and ensures that the DNS configuration always matches the state of your Kubernetes cluster.
- **Scalability**: Great for dynamic environments where services and ingress are frequently changing.

### Summary of the Flow with ExternalDNS:

1. **Client** → sends a request to `my-app.example.com`.
2. **Route 53** → Automatically managed by ExternalDNS, maps `my-app.example.com` to the AWS ELB DNS (`my-elb-12345.region.elb.amazonaws.com`).
3. **AWS ELB** → Receives the traffic and forwards it to the NGINX ingress controller running inside the Kubernetes cluster.
4. **NGINX Ingress Controller** → Matches the request based on the ingress rules and forwards it to the appropriate Kubernetes service.
5. **Service** → Routes traffic to the pod, and the pod processes the request.

Using **ExternalDNS** in this flow simplifies DNS management and ensures that your Route 53 records are always up to date based on the state of your Kubernetes ingress and services.