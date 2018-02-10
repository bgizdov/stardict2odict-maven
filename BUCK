include_defs("//DEFS")

#################################
#      Remote Dependencies      #
#################################
remote_jar(
    name="commons-lang3",
    url="mvn:org.apache.commons:commons-lang3:jar:3.7",
    hash="557edd918fd41f9260963583ebf5a61a43a6b423"
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

############################
#      Actual Library      #
############################
deps = ['//:commons-lang3', '//:dictzip', '//:commons-compress']

java_library(
    name="main-lib",
    srcs=glob(["src/main/**/*.java"]),
    deps=deps
)

java_binary(
    name="main",
    main_class="org.odict.stardict2odict.Main",
    deps=["//:main-lib"]
)