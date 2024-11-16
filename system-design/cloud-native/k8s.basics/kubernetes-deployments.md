When you run `kubectl apply` with a **Deployment spec**, Kubernetes initiates a sequence of actions to create and manage Pods. Here’s a detailed explanation of the process:

---

### **1. `kubectl apply` Command**
- You execute a command like:

  ```bash
  kubectl apply -f deployment.yaml
  ```

  Here, `deployment.yaml` is a manifest that defines the **Deployment** resource, which includes the desired state of your application (e.g., the number of replicas, the container image, labels, etc.).

---

### **2. API Server Receives the Request**
- `kubectl` sends the Deployment spec to the **Kubernetes API Server**.
- The API Server:
    1. Validates the manifest (checks for correctness and completeness).
    2. Authenticates the user and authorizes the request.
    3. Stores the desired state of the Deployment in **etcd** (the cluster's key-value store).

---

### **3. Deployment Controller Creates a ReplicaSet**
- The **Deployment Controller** (running in the Controller Manager) detects the new Deployment resource stored in etcd.
- It creates a **ReplicaSet** object based on the Deployment spec. The ReplicaSet specifies:
    - The desired number of Pod replicas.
    - A template for creating Pods.
    - Labels to identify and manage Pods.

---

### **4. ReplicaSet Controller Creates Pods**
- The **ReplicaSet Controller** (also part of the Controller Manager) monitors the ReplicaSet.
- It calculates the difference between the desired number of Pods (e.g., 3 replicas) and the actual number of running Pods (initially 0).
- To resolve the discrepancy, the ReplicaSet Controller:
    - Generates the specified number of Pod objects.
    - Sends these Pod objects to the API Server.

---

### **5. API Server Schedules Pods**
- The **Scheduler** detects the unscheduled Pods in etcd. For each Pod:
    - The Scheduler identifies the best Node to host the Pod based on:
        - Resource availability (CPU, memory, etc.).
        - Node selectors, taints, tolerations, and affinity rules.
    - The Scheduler updates the Pod spec in etcd to include the selected Node.

---

### **6. Kubelet Deploys Containers**
- On the selected Node, the **kubelet**:
    1. Watches for new Pod assignments via the API Server.
    2. Pulls the container images specified in the Pod spec (via the Container Runtime, e.g., Docker, containerd).
    3. Starts the containers defined in the Pod spec.
    4. Configures networking for the Pod using the **CNI plugin**.
    5. Reports the status of the Pod back to the API Server.

---

### **7. Pod Reaches Running State**
- Once the containers are running and healthy:
    - The kubelet updates the Pod's status in the API Server (e.g., `Running`).
    - The Deployment is now fulfilling its desired state.

---

### **Key Components in Action**
1. **Deployment**: Defines the desired state (e.g., 3 replicas of a web app).
2. **ReplicaSet**: Ensures the specified number of Pods are always running.
3. **Pods**: The smallest deployable unit, running the containers.

---

### **Summary Workflow**
1. `kubectl apply` -> Deployment created.
2. Deployment Controller -> Creates a ReplicaSet.
3. ReplicaSet Controller -> Creates Pods.
4. Scheduler -> Assigns Pods to Nodes.
5. Kubelet -> Pulls images, starts containers, and reports status.

This process ensures that Kubernetes transitions from the **desired state** defined in your Deployment spec to the **actual state** where Pods are running as intended. If any Pods fail, the ReplicaSet Controller will recreate them to maintain the desired state.