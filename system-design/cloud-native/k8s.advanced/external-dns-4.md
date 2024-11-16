# External DNS hands ON

```yaml
apiVersion: v1
kind: Service
metadata:
  name: rog-nginx
  namespace: dmp-system
spec:
  type: ClusterIP 
  ports:
  - port: 80
    name: http
    targetPort: 80
  selector:
    app: nginx
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: rog-nginx
  namespace: dmp-system
spec:
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - image: nginx
        name: nginx
        ports:
        - containerPort: 80
          name: http
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: rog-nginx
  namespace: dmp-system
  annotations:
    external-dns.alpha.kubernetes.io/include: "true"
spec:
  ingressClassName: nginx
  rules:
    - host: dum.dms.sandbox.usw2.ficoanalyticcloud.com
      http:
        paths:
          - backend:
              service:
                name: rog-nginx
                port:
                  number: 80
            path: /
            pathType: Prefix
```

---

## 

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/ef01c46f-446b-4d49-a802-0daa6c440513/f6fbe44b-4be2-4537-89bb-374c0e3e7382/image.png)

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/ef01c46f-446b-4d49-a802-0daa6c440513/9ac7d7f4-9db0-42b3-b735-d39f6ad6c457/image.png)

Reference:

[Automated Creation of Route 53 Records with ExternalDNS | by Rustem Sharipov | Medium](https://medium.com/@russsnizza/automated-creation-of-route53-records-in-a-separate-account-with-externaldns-7a0938631a56)