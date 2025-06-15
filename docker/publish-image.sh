#!/bin/bash
# publish_image.sh
# Script to publish the Docker image to DockerHub.

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Please install it to continue."
    exit 1
fi

# Define image name and tag
IMAGE_NAME="gabim23/webapp-test"
TAG="latest"

# Log in to DockerHub
echo "Logging in to DockerHub..."
docker login
if [ $? -ne 0 ]; then
    echo "DockerHub login failed. Please check your credentials."
    exit 1
fi

echo "Publishing the Docker image ${IMAGE_NAME}:${TAG} to DockerHub..."
docker push ${IMAGE_NAME}:${TAG}
if [ $? -eq 0 ]; then
    echo "The Docker image has been successfully published to DockerHub."
else
    echo "Error publishing the Docker image."
    exit 1
fi
