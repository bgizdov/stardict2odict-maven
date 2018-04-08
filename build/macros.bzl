def remote_jar(name, url, hash, deps = []):
    native.remote_file(
        name = name + "-source",
        out = name + "-source.jar",
        url = url,
        sha1 = hash
    )
    native.prebuilt_jar(
        name = name,
        binary_jar = ":" + name + "-source",
        deps=deps,
        visibility=["PUBLIC"]
    )

def remote_zipped_jar(name,  url, hash, path, deps = []):
    native.genrule(
        name = name + "-nested",
        out = name + ".jar",
        cmd = "cp -a $(location //:" + name + "-source)/" + path + ".jar $OUT"
    )
    native.remote_file(
        name = name + "-source",
        url = url,
        type = 'exploded_zip',
        sha1 = hash
    )
    native.prebuilt_jar(
        name = name,
        binary_jar = ":" + name + "-nested",
        deps=deps,
        visibility=["PUBLIC"]
    )