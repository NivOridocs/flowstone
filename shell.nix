{ pkgs ? import (fetchTarball "https://github.com/NixOS/nixpkgs/archive/e06bd4b64bbfda91d74f13cb5eca89485d47528f.tar.gz") {}}:

pkgs.mkShell {
  nativeBuildInputs = [
    pkgs.gitflow
    pkgs.jdk
    pkgs.java-language-server
  ];
}
