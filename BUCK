load("//build:macros.bzl", "remote_jar", "remote_zipped_jar")

#################################
#      Remote Dependencies      #
#################################
remote_jar(
    name="commons-lang3",
    url="mvn:org.apache.commons:commons-lang3:jar:3.7",
    hash="557edd918fd41f9260963583ebf5a61a43a6b423"
)

remote_jar(
    name="commons-text",
    url="mvn:org.apache.commons:commons-text:jar:1.3",
    hash="9abf61708a66ab5e55f6169a200dbfc584b546d9"
)

remote_jar(
    name="dictzip",
    url="mvn:org.dict.zip:dictzip-lib:jar:0.8.2",
    hash="08a3f9e0926832259f403a49f7461d14599dea9f"
)

remote_jar(
    name="commons-compress",
    url="mvn:org.apache.commons:commons-compress:jar:1.16",
    hash="2d874b2ecf9de74437edcfbd5138b168e9ca0d14"
)

remote_jar(
    name="jackson-dataformat-yaml",
    url="mvn:com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:jar:2.9.4",
    hash="3edaa0c22529b6c1c095f6e3cafc6d54e8709538"
)

remote_jar(
    name="jackson-core",
    url="mvn:com.fasterxml.jackson.core:jackson-core:jar:2.9.4",
    hash="a9a71ec1aa37da47db168fede9a4a5fb5e374320"
)

remote_jar(
    name="jackson-databind",
    url="mvn:com.fasterxml.jackson.core:jackson-databind:jar:2.9.4",
    hash="498bbc3b94f566982c7f7c6d4d303fce365529be"
)

remote_jar(
    name="jackson-annotations",
    url="mvn:com.fasterxml.jackson.core:jackson-annotations:jar:2.9.4",
    hash="1380b592ad70439346b5d954ad202be048451c5a"
)

remote_jar(
    name="snakeyaml",
    url="mvn:org.yaml:snakeyaml:jar:1.20",
    hash="11e7e64e621e5e43c7481bf01072a7b1597d4f03"
)

remote_jar(
    name="emoji-java",
    url="mvn:com.vdurmont:emoji-java:jar:4.0.0",
    hash="b2de319e06192443ee273dd7e69d10b09ffb3442"
)

remote_jar(
    name="json",
    url="mvn:org.json:json:jar:20180130",
    hash="26ba2ec0e791a32ea5dfbedfcebf36447ee5b12c"
)

remote_zipped_jar(
    name="commons-cli",
    url="http://mirrors.sorengard.com/apache//commons/cli/binaries/commons-cli-1.4-bin.zip",
    hash="5d27df91a432d8f49f2ccaebd89ae6977186e4ed",
    path="commons-cli-1.4/commons-cli-1.4"
)

############################
#      Actual Library      #
############################
java_binary(
    name="main",
    main_class="org.odict.stardict2odict.Main",
    deps=["//src/main:main-lib"]
)