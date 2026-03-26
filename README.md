# Run llama.cpp on yocto linux for Jetson Nano (4GB) 

This project is a fork/adaptation of [https://github.com/kreier/llama.cpp-jetson](llama.cpp-jetson)

## 1. Checkout yocto layers for jetson nano
```
$ mkdir ~/jetson-nano-yocto

$ cd ~/jetson-nano-yocto
$ git clone git://git.yoctoproject.org/poky
$ cd poky
$ git checkout -t origin/kirkstone -b kirkstone

$ cd ~/jetson-nano-yocto
$ git clone https://github.com/OE4T/meta-tegra.git
$ cd meta-tegra
$ git checkout -t origin/kirkstone-l4t-r32.7.x -b kirkstone

$ cd ~/jetson-nano-yocto
$ git clone https://github.com/tqm-dev/meta-jetson-llama-cpp

$ cd ~/jetson-nano-yocto
$ . poky/oe-init-build-env
jetson-nano-yocto/build$ 
```

## 2. Edit ~/jetson-nano-yocto/build/conf/local.conf
```
MACHINE = "jetson-nano-devkit"
TARGET_ARCH = "aarch64"
IMAGE_FSTYPES += "tegraflash"                  
IMAGE_INSTALL:append = " llama-cpp gguf-model"
```

## 3. Add your layers to ~/jetson-nano-yocto/build/conf/bblayers.conf
```
BBLAYERS ?= " \
  /path/to/jetson-nano-yocto/poky/meta \
  /path/to/jetson-nano-yocto/poky/meta-poky \
  /path/to/jetson-nano-yocto/poky/meta-yocto-bsp \
  /path/to/jetson-nano-yocto/meta-tegra \            << Add
  /path/to/jetson-nano-yocto/meta-jetson-llama-cpp \ << Add
  "
```

## 4. Build
```
// Edit AppArmor setting if you need
$ echo 0 | sudo tee /proc/sys/kernel/apparmor_restrict_unprivileged_userns

// Bitbake!
$ cd ~/jetson-nano-yocto/build
$ bitbake core-image-minimal
```

## 5. Boot Jetson nano (force recovery mode)

- Insert MicroSD card to jetson nano module.
- Into force recovery mode using jumper pin.
- Connect to PC via usb.
- Detect jetson as APX mode:
```
$ lsusb
Bus 001 Device 015: ID 0955:7f21 NVIDIA Corp. APX
```

## 6. Flash boot image
```
$ cd ~/jetson-nano-yocto
$ mkdir -p tegraflash
$ cd tegraflash
$ cp ../build/tmp/deploy/images/jetson-nano-devkit/core-image-minimal-jetson-nano-devkit.tegraflash.tar.gz ./
$ tar xvf core-image-minimal-jetson-nano-devkit.tegraflash.tar.gz
$ sudo ./doflash.sh
```

## 7. Reboot Jetson nano and run llama.cpp

#### Check if llama-cli is available:
```
Poky (Yocto Project Reference Distro) 4.0.34 jetson-nano-devkit /dev/ttyS0

jetson-nano-devkit login: root
root@jetson-nano-devkit:~# llama-cli --version
ggml_cuda_init: GGML_CUDA_FORCE_MMQ:    no
ggml_cuda_init: GGML_CUDA_FORCE_CUBLAS: no
ggml_cuda_init: found 1 CUDA devices:
  Device 0: NVIDIA Tegra X1, compute capability 5.3, VMM: no
version: 5050 (23106f94ea)
built with aarch64-poky-linux-gcc (GCC) 11.5.0 for aarch64-poky-linux
```

#### Run llama-cli with the specific model:
```
root@jetson-nano-devkit:~# llama-cli -m /usr/share/llama-cpp/models/gemma-3-1b-it-Q4_K_M.gguf --n-gpu-layers 99
ggml_cuda_init: GGML_CUDA_FORCE_MMQ:    no
ggml_cuda_init: GGML_CUDA_FORCE_CUBLAS: no
ggml_cuda_init: found 1 CUDA devices:
  Device 0: NVIDIA Tegra X1, compute capability 5.3, VMM: no
build: 5050 (23106f94ea) with aarch64-poky-linux-gcc (GCC) 11.5.0 for aarch64-poky-linux
main: llama backend init
main: load the model and apply lora adapter, if any
llama_model_load_from_file_impl: using device CUDA0 (NVIDIA Tegra X1) - 3600 MiB free
llama_model_loader: loaded meta data with 38 key-value pairs and 340 tensors from /usr/share/llama-cpp/models/gemma-3-1b-it-Q4_K_M.gguf (version GGUF V3 (latest))
llama_model_loader: Dumping metadata keys/values. Note: KV overrides do not apply in this output.
llama_model_loader: - kv   0:                       general.architecture str              = gemma3
llama_model_loader: - kv   1:                               general.type str              = model
llama_model_loader: - kv   2:                               general.name str              = Gemma 3 1b It
llama_model_loader: - kv   3:                           general.finetune str              = it
llama_model_loader: - kv   4:                           general.basename str              = gemma-3
llama_model_loader: - kv   5:                         general.size_label str              = 1B
llama_model_loader: - kv   6:                            general.license str              = gemma
llama_model_loader: - kv   7:                   general.base_model.count u32              = 1
llama_model_loader: - kv   8:                  general.base_model.0.name str              = Gemma 3 1b Pt
llama_model_loader: - kv   9:          general.base_model.0.organization str              = Google
llama_model_loader: - kv  10:              general.base_model.0.repo_url str              = https://huggingface.co/google/gemma-3...
llama_model_loader: - kv  11:                               general.tags arr[str,1]       = ["text-generation"]
llama_model_loader: - kv  12:                      gemma3.context_length u32              = 32768
llama_model_loader: - kv  13:                    gemma3.embedding_length u32              = 1152
llama_model_loader: - kv  14:                         gemma3.block_count u32              = 26
llama_model_loader: - kv  15:                 gemma3.feed_forward_length u32              = 6912
llama_model_loader: - kv  16:                gemma3.attention.head_count u32              = 4
llama_model_loader: - kv  17:    gemma3.attention.layer_norm_rms_epsilon f32              = 0.000001
llama_model_loader: - kv  18:                gemma3.attention.key_length u32              = 256
llama_model_loader: - kv  19:              gemma3.attention.value_length u32              = 256
llama_model_loader: - kv  20:                      gemma3.rope.freq_base f32              = 1000000.000000
llama_model_loader: - kv  21:            gemma3.attention.sliding_window u32              = 512
llama_model_loader: - kv  22:             gemma3.attention.head_count_kv u32              = 1
llama_model_loader: - kv  23:                       tokenizer.ggml.model str              = llama
llama_model_loader: - kv  24:                         tokenizer.ggml.pre str              = default
llama_model_loader: - kv  25:                      tokenizer.ggml.tokens arr[str,262144]  = ["<pad>", "<eos>", "<bos>", "<unk>", ...
llama_model_loader: - kv  26:                      tokenizer.ggml.scores arr[f32,262144]  = [-1000.000000, -1000.000000, -1000.00...
llama_model_loader: - kv  27:                  tokenizer.ggml.token_type arr[i32,262144]  = [3, 3, 3, 3, 3, 4, 3, 3, 3, 3, 3, 3, ...
llama_model_loader: - kv  28:                tokenizer.ggml.bos_token_id u32              = 2
llama_model_loader: - kv  29:                tokenizer.ggml.eos_token_id u32              = 1
llama_model_loader: - kv  30:            tokenizer.ggml.unknown_token_id u32              = 3
llama_model_loader: - kv  31:            tokenizer.ggml.padding_token_id u32              = 0
llama_model_loader: - kv  32:               tokenizer.ggml.add_bos_token bool             = true
llama_model_loader: - kv  33:               tokenizer.ggml.add_eos_token bool             = false
llama_model_loader: - kv  34:                    tokenizer.chat_template str              = {{ bos_token }}\n{%- if messages[0]['r...
llama_model_loader: - kv  35:            tokenizer.ggml.add_space_prefix bool             = false
llama_model_loader: - kv  36:               general.quantization_version u32              = 2
llama_model_loader: - kv  37:                          general.file_type u32              = 15
llama_model_loader: - type  f32:  157 tensors
llama_model_loader: - type q5_0:  117 tensors
llama_model_loader: - type q8_0:   14 tensors
llama_model_loader: - type q4_K:   39 tensors
llama_model_loader: - type q6_K:   13 tensors
print_info: file format = GGUF V3 (latest)
print_info: file type   = Q4_K - Medium
print_info: file size   = 762.49 MiB (6.40 BPW) 
load: special_eos_id is not in special_eog_ids - the tokenizer config may be incorrect
load: special tokens cache size = 6414
load: token to piece cache size = 1.9446 MB
print_info: arch             = gemma3
print_info: vocab_only       = 0
print_info: n_ctx_train      = 32768
print_info: n_embd           = 1152
print_info: n_layer          = 26
print_info: n_head           = 4
print_info: n_head_kv        = 1
print_info: n_rot            = 256
print_info: n_swa            = 512
print_info: n_swa_pattern    = 6
print_info: n_embd_head_k    = 256
print_info: n_embd_head_v    = 256
print_info: n_gqa            = 4
print_info: n_embd_k_gqa     = 256
print_info: n_embd_v_gqa     = 256
print_info: f_norm_eps       = 0.0e+00
print_info: f_norm_rms_eps   = 1.0e-06
print_info: f_clamp_kqv      = 0.0e+00
print_info: f_max_alibi_bias = 0.0e+00
print_info: f_logit_scale    = 0.0e+00
print_info: f_attn_scale     = 6.2e-02
print_info: n_ff             = 6912
print_info: n_expert         = 0
print_info: n_expert_used    = 0
print_info: causal attn      = 1
print_info: pooling type     = 0
print_info: rope type        = 2
print_info: rope scaling     = linear
print_info: freq_base_train  = 1000000.0
print_info: freq_scale_train = 1
print_info: n_ctx_orig_yarn  = 32768
print_info: rope_finetuned   = unknown
print_info: ssm_d_conv       = 0
print_info: ssm_d_inner      = 0
print_info: ssm_d_state      = 0
print_info: ssm_dt_rank      = 0
print_info: ssm_dt_b_c_rms   = 0
print_info: model type       = 1B
print_info: model params     = 999.89 M
print_info: general.name     = Gemma 3 1b It
print_info: vocab type       = SPM
print_info: n_vocab          = 262144
print_info: n_merges         = 0
print_info: BOS token        = 2 '<bos>'
print_info: EOS token        = 1 '<eos>'
print_info: EOT token        = 106 '<end_of_turn>'
print_info: UNK token        = 3 '<unk>'
print_info: PAD token        = 0 '<pad>'
print_info: LF token         = 248 '<0x0A>'
print_info: EOG token        = 1 '<eos>'
print_info: EOG token        = 106 '<end_of_turn>'
print_info: max token length = 48
load_tensors: loading model tensors, this can take a while... (mmap = true)
load_tensors: offloading 26 repeating layers to GPU
load_tensors: offloading output layer to GPU
load_tensors: offloaded 27/27 layers to GPU
load_tensors:        CUDA0 model buffer size =   762.54 MiB
load_tensors:   CPU_Mapped model buffer size =   306.00 MiB
.............................................
llama_context: constructing llama_context
llama_context: n_seq_max     = 1
llama_context: n_ctx         = 4096
llama_context: n_ctx_per_seq = 4096
llama_context: n_batch       = 2048
llama_context: n_ubatch      = 512
llama_context: causal_attn   = 1
llama_context: flash_attn    = 0
llama_context: freq_base     = 1000000.0
llama_context: freq_scale    = 1
llama_context: n_ctx_per_seq (4096) < n_ctx_train (32768) -- the full capacity of the model will not be utilized
llama_context:  CUDA_Host  output buffer size =     1.00 MiB
init: kv_size = 4096, offload = 1, type_k = 'f16', type_v = 'f16', n_layer = 26, can_shift = 1
init:      CUDA0 KV buffer size =   104.00 MiB
llama_context: KV self size  =  104.00 MiB, K (f16):   52.00 MiB, V (f16):   52.00 MiB
llama_context:      CUDA0 compute buffer size =   514.25 MiB
llama_context:  CUDA_Host compute buffer size =    18.26 MiB
llama_context: graph nodes  = 1099
llama_context: graph splits = 2
common_init_from_params: setting dry_penalty_last_n to ctx_size = 4096
common_init_from_params: warming up the model with an empty run - please wait ... (--no-warmup to disable)
main: llama threadpool init, n_threads = 4
main: chat template is available, enabling conversation mode (disable it with -no-cnv)
main: chat template example:
<start_of_turn>user
You are a helpful assistant

Hello<end_of_turn>
<start_of_turn>model
Hi there<end_of_turn>
<start_of_turn>user
How are you?<end_of_turn>
<start_of_turn>model


system_info: n_threads = 4 (n_threads_batch = 4) / 4 | CUDA : USE_GRAPHS = 1 | PEER_MAX_BATCH_SIZE = 128 | CPU : NEON = 1 | ARM_FMA = 1 | LLAMAFILE = 1 | OPENMP = 1 | AARCH64_REPACK = 1 | 

main: interactive mode on.
sampler seed: 1863036176
sampler params: 
	repeat_last_n = 64, repeat_penalty = 1.000, frequency_penalty = 0.000, presence_penalty = 0.000
	dry_multiplier = 0.000, dry_base = 1.750, dry_allowed_length = 2, dry_penalty_last_n = 4096
	top_k = 40, top_p = 0.950, min_p = 0.050, xtc_probability = 0.000, xtc_threshold = 0.100, typical_p = 1.000, top_n_sigma = -1.000, temp = 0.800
	mirostat = 0, mirostat_lr = 0.100, mirostat_ent = 5.000
sampler chain: logits -> logit-bias -> penalties -> dry -> top-k -> typical -> top-p -> min-p -> xtc -> temp-ext -> dist 
generate: n_ctx = 4096, n_batch = 2048, n_predict = -1, n_keep = 1

== Running in interactive mode. ==
 - Press Ctrl+C to interject at any time.
 - Press Return to return control to the AI.
 - To return control without starting a new line, end your input with '/'.
 - If you want to submit another line, end your input with '\'.
 - Not using system message. To change it, set a different value via -sys PROMPT


> who are you ?
Hi there! I’m Gemma, a large language model created by the Gemma team at Google DeepMind. I’m an open-weights model, which means I’m publicly available for use. 

I’m here to help you with a variety of text-based tasks, like answering your questions, writing stories, and more. 

How can I help you today?

>
```
