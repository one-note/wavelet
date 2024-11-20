If you already have a Kubernetes manifest file (e.g., `kubespec.yaml`) with a placeholder for the image, you can dynamically replace the placeholder with the actual Docker image URI during deployment using `kubectl apply`. Here's how you can adjust the GitHub Actions workflow to achieve this:

---

### **Updated GitHub Actions Workflow**

```yaml
name: Build and Deploy to EKS

on:
  push:
    branches:
      - main

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      # Checkout code
      - name: Checkout Code
        uses: actions/checkout@v3

      # Set up AWS CLI and kubectl
      - name: Configure AWS CLI
        uses: aws-actions/configure-aws-credentials@v3
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ secrets.AWS_REGION }}

      - name: Install kubectl
        uses: azure/setup-kubectl@v3
        with:
          kubectl-version: 'latest'

      - name: Configure kubectl for EKS
        run: |
          aws eks update-kubeconfig --region ${{ secrets.AWS_REGION }} --name ${{ secrets.CLUSTER_NAME }}

      # Build Docker Image
      - name: Log in to Amazon ECR
        run: |
          aws ecr get-login-password --region ${{ secrets.AWS_REGION }} | docker login --username AWS --password-stdin "${{ secrets.AWS_ACCOUNT_ID }}.dkr.ecr.${{ secrets.AWS_REGION }}.amazonaws.com"

      - name: Build and Tag Docker Image
        run: |
          IMAGE_URI="${{ secrets.AWS_ACCOUNT_ID }}.dkr.ecr.${{ secrets.AWS_REGION }}.amazonaws.com/${{ secrets.ECR_REPOSITORY }}:latest"
          docker build -t "$IMAGE_URI" .
          echo "$IMAGE_URI" > image_uri.txt

      - name: Push Docker Image to ECR
        run: |
          IMAGE_URI=$(cat image_uri.txt)
          docker push "$IMAGE_URI"

      # Replace placeholder and deploy to Kubernetes
      - name: Replace Image Placeholder and Apply
        run: |
          IMAGE_URI=$(cat image_uri.txt)
          sed -i "s|<image-placeholder>|$IMAGE_URI|g" kubespec.yaml
          kubectl apply -f kubespec.yaml

      - name: Verify Deployment
        run: |
          kubectl rollout status deployment/<your-deployment-name> --namespace <your-namespace>
```

---

### **Steps Explained:**

1. **Checkout Code**:
   - Retrieves the `kubespec.yaml` file from your repository.

2. **Build and Push Docker Image**:
   - Builds the Docker image.
   - Pushes it to Amazon ECR with a `latest` tag.

3. **Replace Image Placeholder**:
   - The `sed` command replaces the `<image-placeholder>` in `kubespec.yaml` with the actual Docker image URI from Amazon ECR.
   - Example of placeholder in `kubespec.yaml`:
     ```yaml
     containers:
       - name: my-app
         image: <image-placeholder>
     ```
   - After replacement:
     ```yaml
     containers:
       - name: my-app
         image: 123456789012.dkr.ecr.us-east-1.amazonaws.com/my-repo:latest
     ```

4. **Apply to Kubernetes**:
   - Uses `kubectl apply -f kubespec.yaml` to deploy the updated manifest to the EKS cluster.

5. **Verify Deployment**:
   - Ensures the deployment rolls out successfully using `kubectl rollout status`.

---

### **Note**:
- Replace `<your-deployment-name>` and `<your-namespace>` in the `kubectl rollout status` command with the actual deployment name and namespace used in your `kubespec.yaml`.
- Ensure the `kubespec.yaml` file is included in your repository root or adjust the path accordingly.
