SUMMARY = "Pre-trained GGUF model for llama.cpp"

SRC_URI = "https://huggingface.co/ggml-org/gemma-3-1b-it-GGUF/resolve/main/gemma-3-1b-it-Q4_K_M.gguf?download=true"
SRC_URI[sha256sum] = "8ccc5cd1f1b3602548715ae25a66ed73fd5dc68a210412eea643eb20eb75a135"
LICENSE = "CLOSED"

S = "${WORKDIR}"

MODEL_DIR = "/usr/share/llama-cpp/models"

do_install() {
    install -d ${D}${MODEL_DIR}
    install -m 0644 ${S}/gemma-3-1b-it-Q4_K_M.gguf ${D}${MODEL_DIR}/
}

FILES:${PN} += "${MODEL_DIR}/*.gguf"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
