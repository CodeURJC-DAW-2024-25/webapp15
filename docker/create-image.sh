#!/bin/bash
# create_image.sh
# Script to build the Docker image for the Spring Boot application using the Dockerfile.

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Please install it to continue."
    exit 1
fi

# Define image name and tag
IMAGE_NAME="gabim23/webapp-test"
TAG="latest"

echo "Building the Docker image ${IMAGE_NAME}:${TAG}..."
cd ..
docker build -t ${IMAGE_NAME}:${TAG} -f ./docker/Dockerfile .
if [ $? -eq 0 ]; then
    echo "The Docker image ${IMAGE_NAME}:${TAG} has been successfully built."
else
    echo "Error building the Docker image."
    exit 1
fi
