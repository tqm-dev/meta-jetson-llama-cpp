# Build yocto linux for Jetson Nano (4GB)

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

## 5. Prepare jetson nano 

- Insert MicroSD card to jetson nano module.
- Into force flash recovery mode.
- Connect to PC via usb.
- Detect jetson as APX mode:
```
$ lsusb
Bus 001 Device 015: ID 0955:7f21 NVIDIA Corp. APX
```

## 6. Flash
```
$ cd ~/jetson-nano-yocto
$ mkdir -p tegraflash
$ cd tegraflash
$ cp ../build/tmp/deploy/images/jetson-nano-devkit/core-image-minimal-jetson-nano-devkit.tegraflash.tar.gz ./
$ tar xvf core-image-minimal-jetson-nano-devkit.tegraflash.tar.gz
$ sudo ./doflash.sh
```
