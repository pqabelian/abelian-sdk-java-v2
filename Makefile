CACHE_DIR=.cache
LIBABELSDK_DIST_URL=https://github.com/pqabelian/abelian-sdk-go-v2/releases/download/v2.0.1/libabelsdk-2.0.1.tar.gz
LIBABELSDK_DIST_NAME=libabelsdk-2.0.1
LIBABELSDK_DIST_FILE=$(LIBABELSDK_DIST_NAME).tar.gz
RESOURCES_DIR=abel4j/src/main/resources
RESOURCES_NATIVE_DIR=$(RESOURCES_DIR)/native

build: build-resources
	./gradlew build

clean:
	./gradlew clean

clean-all: clean clean-resources

build-resources: $(RESOURCES_NATIVE_DIR)

clean-resources:
	rm -rf $(RESOURCES_NATIVE_DIR)

$(CACHE_DIR):
	mkdir -p $@

$(CACHE_DIR)/$(LIBABELSDK_DIST_FILE): | $(CACHE_DIR)
	curl -L $(LIBABELSDK_DIST_URL) -o $(CACHE_DIR)/$(LIBABELSDK_DIST_FILE)

$(RESOURCES_NATIVE_DIR): $(CACHE_DIR)/$(LIBABELSDK_DIST_FILE)
	@if [ ! -d $(RESOURCES_NATIVE_DIR) ]; then \
		echo "Extracting libabelsdk to $(RESOURCES_NATIVE_DIR) ..."; \
		rm -rf $(RESOURCES_NATIVE_DIR) $(RESOURCES_NATIVE_DIR); \
		tar -C $(CACHE_DIR) -xzf $(CACHE_DIR)/$(LIBABELSDK_DIST_FILE); \
		mv $(CACHE_DIR)/$(LIBABELSDK_DIST_NAME) $(RESOURCES_NATIVE_DIR); \
	fi

demo:
	./gradlew run --args="AbecRPCClient"

demo-debug:
	./gradlew runDebug --args="ScanCoins 300735 300735"
