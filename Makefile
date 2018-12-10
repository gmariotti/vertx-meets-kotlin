service-up-compose = docker-compose -f docker/base.yml

clean:
	rm -rf docker/*/build
	./gradlew clean

build: clean
	./gradlew shadowJar

compose.cluster.up: build
	$(service-up-compose) build
	$(service-up-compose) up
	$(MAKE) -f $(CURRENT_FILE) compose.cluster.down

compose.cluster.down:
	$(service-up-compose) down
