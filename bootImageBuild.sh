# bin/bash
docker stop gateway-service || true
docker rm gateway-service || true
docker rmi leesg107/gateway-service || true
./gradlew clean bootBuildImage --imageName=leesg107/gateway-service
