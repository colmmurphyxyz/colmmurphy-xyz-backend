{
  description = "Developer Environment.";
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils}:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config.allowUnfree = true;
        };
        java = pkgs.jdk21_headless;
        gradle = pkgs.gradle_9;
        kotlin = pkgs.kotlin;
        intellij = pkgs.jetbrains.idea-community;
      in {
        devShells.default = pkgs.mkShell {
          name = "spring boot + kotlin dev";
          buildInputs = [
            gradle
            intellij
            java
            kotlin
          ];
          shellHook = ''
            export JAVA_HOME=$(dirname $(dirname $(which java)))

            export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:${
              pkgs.lib.makeLibraryPath [
                kotlin
                pkgs.libGL
                pkgs.xorg.libX11
                pkgs.fontconfig
              ]
            };

            echo "Activated"
          '';
        };
      }
    );
}

