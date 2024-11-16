## External DNS with AWS in my company

1. create a svc of type load balancer
2. create a ingress that maps the hostname to above load balancer
3. As the ingress has external-dns annotation added, it registers teh R53 mapping with host and elb entry

k get ing -n dmp-system manager -o yaml
Warning: Use tokens from the TokenRequest API or manually created secret-based tokens instead of auto-generated secret-based tokens.
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
annotations:
external-dns.alpha.kubernetes.io/target: 8whci8c.impervadns.net
meta.helm.sh/release-name: dmp-manager
meta.helm.sh/release-namespace: dmp-system
nginx.ingress.kubernetes.io/configuration-snippet: |
more_set_headers "X-Xss-Protection: 0";
more_set_headers "X-Content-Type-Options: nosniff";
nginx.ingress.kubernetes.io/service-upstream: "true"
sidecar.istio.io/proxyCPU: 1000m
sidecar.istio.io/proxyMemory: 2048Mi
creationTimestamp: "2023-07-20T09:43:15Z"
generation: 1
labels:
app.kubernetes.io/managed-by: Helm
com.fico.dmp/enable-waf: "true"
name: manager
namespace: dmp-system
resourceVersion: "1521246234"
uid: b5ab91a2-e362-467f-b3c1-3b86e5d665a8
spec:
ingressClassName: nginx
rules:
- host: console.dms.sandbox.usw2.ficoanalyticcloud.com
  http:
  paths:
    - backend:
      service:
      name: manager
      port:
      number: 8080
      path: /com.fico.dmp.manager
      pathType: ImplementationSpecific
      tls:
- hosts:
    - console.dms.sandbox.usw2.ficoanalyticcloud.com
      status:
      loadBalancer:
      ingress:
    - hostname: k8s-ingressn-ingressn-6de671a808-d0e38473329d0a44.elb.us-west-2.amazonaws.com
```

k get svc -n ingress-nginx ingress-nginx-nlb -o yaml
Warning: Use tokens from the TokenRequest API or manually created secret-based tokens instead of auto-generated secret-based tokens.
```yaml
apiVersion: v1
kind: Service
metadata:
annotations:
meta.helm.sh/release-name: ingress-nginx
meta.helm.sh/release-namespace: ingress-nginx
service.beta.kubernetes.io/aws-load-balancer-cross-zone-load-balancing-enabled: "false"
service.beta.kubernetes.io/aws-load-balancer-healthcheck-port: "443"
service.beta.kubernetes.io/aws-load-balancer-healthcheck-protocol: TCP
service.beta.kubernetes.io/aws-load-balancer-proxy-protocol: '*'
service.beta.kubernetes.io/aws-load-balancer-scheme: internet-facing
service.beta.kubernetes.io/aws-load-balancer-type: nlb-ip
creationTimestamp: "2022-08-10T19:44:19Z"
finalizers:
- service.k8s.aws/resources
- service.kubernetes.io/load-balancer-cleanup
  labels:
  app.kubernetes.io/component: controller
  app.kubernetes.io/managed-by: Helm
  app.kubernetes.io/name: ingress-nginx
  app.kubernetes.io/part-of: ingress-nginx
  app.kubernetes.io/version: 1.8.0
  com.fico.iod/created-by: iod-operator
  com.fico.iod/processed: "true"
  helm.sh/chart: ingress-nginx-4.7.0
  name: ingress-nginx-nlb
  namespace: ingress-nginx
  resourceVersion: "4630643701"
  uid: 4b29ff6c-21d0-409f-b672-d65113bac6a9
  spec:
  allocateLoadBalancerNodePorts: true
  clusterIP: 172.20.133.132
  clusterIPs:
- 172.20.133.132
  externalTrafficPolicy: Local
  healthCheckNodePort: 30815
  internalTrafficPolicy: Cluster
  ipFamilies:
- IPv4
  ipFamilyPolicy: SingleStack
  ports:
- name: http
  nodePort: 31766
  port: 80
  protocol: TCP
  targetPort: http
- name: https
  nodePort: 30107
  port: 443
  protocol: TCP
  targetPort: https
  selector:
  app.kubernetes.io/name: ingress-nginx
  app.kubernetes.io/part-of: ingress-nginx
  sessionAffinity: None
  type: LoadBalancer
  status:
  loadBalancer:
  ingress:
    - hostname: k8s-ingressn-ingressn-6de671a808-d0e38473329d0a44.elb.us-west-2.amazonaws.com
```