SUMMARY = "llama.cpp for jetson nano"
DESCRIPTION = "LLM inference in C/C++ for jetson nano"

S = "${WORKDIR}/git"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=1539dadbedb60aa18519febfeab70632"

SRC_URI = "\
    git://github.com/ggml-org/llama.cpp.git;protocol=https;branch=master \
    file://PATCH-llamab5050-jetson-nano.patch \
    file://cuda_bf16.h \
    file://cuda_bf16.hpp \
"

# b5050
SRCREV = "23106f94ea2bc3da929afb7330655fd5515d08dc"

DEPENDS = " openssl cuda-toolkit curl"

RDEPENDS:${PN} += " \
    cuda-libraries \
    cuda-cudart \
    libcublas \
    libstdc++ \
    curl \
"

do_configure:prepend() {
    # Bfloat16 headers for llama.cpp compilation
    install -m 0644 ${WORKDIR}/cuda_bf16.h ${STAGING_DIR_TARGET}/usr/local/cuda-10.2/include
    install -m 0644 ${WORKDIR}/cuda_bf16.hpp ${STAGING_DIR_TARGET}/usr/local/cuda-10.2/include
}

inherit cmake cuda

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DGGML_CUDA=ON \
    -DLLAMA_CURL=ON \
    -DCMAKE_CUDA_STANDARD=14 \
    -DCMAKE_CUDA_STANDARD_REQUIRED=true \
    -DGGML_CPU_ARM_ARCH=armv8-a \
    -DGGML_NATIVE=off \
"

SOLIBS = ".so"
SOLIBSDEV = ""

FILES:${PN} = " \
    ${bindir}/* \
    ${libdir}/* \
"

FILES:${PN}-dev = " \
    ${includedir}/* \
"

